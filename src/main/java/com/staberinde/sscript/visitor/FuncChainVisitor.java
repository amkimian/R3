package com.staberinde.sscript.visitor;

import com.staberinde.sscript.SScriptLexer;
import com.staberinde.sscript.SScriptParser;
import com.staberinde.sscript.block.FilterBlock;
import com.staberinde.sscript.block.MapBlock;
import com.staberinde.sscript.block.ReduceBlock;
import com.staberinde.sscript.exception.SSException;
import com.staberinde.sscript.exception.SSParseException;
import com.staberinde.sscript.model.NamedParameter;
import com.staberinde.sscript.model.SParseContext;
import com.staberinde.sscript.program.CoreBlock;
import com.staberinde.sscript.program.DynamicRegistry;
import com.staberinde.sscript.value.BlockValueBoolean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class FuncChainVisitor extends ContextualVisitor<CoreBlock>{
    private NamedParameter prevParam;

    FuncChainVisitor(final SParseContext parseContext) {
        super(parseContext);
    }

    @Override
    public CoreBlock visitChainExpr(SScriptParser.ChainExprContext ctx) {
        this.prevParam = new NamedParameter(this.visit(ctx.funcChain(0)));
        return this.visit(ctx.funcChain(1));
    }

    @Override
    public CoreBlock visitCallExpr(SScriptParser.CallExprContext ctx) {
        final List<NamedParameter> params = new ArrayList<>();
        if (this.prevParam != null) {
            params.add(this.prevParam);
        }
        final NamedParameterVisitor npv = new NamedParameterVisitor(this.parseContext);
        final List<SScriptParser.NamedParameterContext> paramContexts = ctx.func == null ? ctx.proc.params : ctx.func.params;
        for(SScriptParser.NamedParameterContext npc : paramContexts) {
            params.add(npv.visitNamedParameter(npc));
        }
        if (ctx.func != null) {
            if (ctx.func.name != null) {
                return DynamicRegistry.constructFrom(ctx.start, ctx.func.name.getText(), params);
            } else {
                return handleBuiltIn(ctx.func);
            }
        } else {
            final AtomicReference<CoreBlock> callRef = this.parseContext.registerProcedureCall(ctx.proc, params);
            return context -> Optional.ofNullable(callRef.get()).map(callBlock -> callBlock.run(context)).orElseThrow(()-> new SSException("Procedure call failed"));
        }
    }

    private CoreBlock handleBuiltIn(SScriptParser.GeneralFuncContext ctx) {
        if (ctx.mfrExpr() != null) {
            return handleMFR(ctx.mfrExpr());
        } else {
            return context -> {
                try {
                    context.getVariable(ctx.isDef().var.getText());
                    return BlockValueBoolean.TRUE;
                } catch (Exception e) {
                    return BlockValueBoolean.FALSE;
                }
            };
        }
    }

    private CoreBlock handleMFR(final SScriptParser.MfrExprContext ctx) {
        final ExpressionVisitor ev = new ExpressionVisitor(this.parseContext);
        final LambdaVisitor lv = new LambdaVisitor(this.parseContext);
        final CoreBlock target = ev.visitExpression(ctx.exp);
        final CoreBlock lambda = lv.visitLambdaExpression(ctx.lambda);

        switch(ctx.type.getType()) {
            case SScriptLexer.MAP:
                return new MapBlock(ctx.start, target, lambda);
                case SScriptLexer.FILTER:
                return new FilterBlock(ctx.start, target, lambda);
                case SScriptLexer.REDUCE:
            default:
                if (ctx.initial == null) {
                    throw new SSParseException("Initial value required for reduce");
                }
                return new ReduceBlock(ctx.start, target,ev.visitExpression(ctx.initial), lambda);
        }
    }
}
