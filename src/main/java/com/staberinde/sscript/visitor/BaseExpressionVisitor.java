package com.staberinde.sscript.visitor;

import com.staberinde.sscript.SScriptParser;
import com.staberinde.sscript.block.LambdaCallBlock;
import com.staberinde.sscript.exception.SSParseException;
import com.staberinde.sscript.model.SParseContext;
import com.staberinde.sscript.program.CoreBlock;

import java.util.List;
import java.util.ArrayList;

public class BaseExpressionVisitor extends ContextualVisitor<CoreBlock>{
    BaseExpressionVisitor(final SParseContext parseContext) {
        super(parseContext);
    }

    @Override
    public CoreBlock visitBaseExpr(SScriptParser.BaseExprContext ctx) {
        if (ctx.unaryExpr() != null) {
            final UnaryExpressionVisitor uev = new UnaryExpressionVisitor(this.parseContext);
            return uev.visit(ctx.unaryExpr());
        }
        if (ctx.lambdaCall() != null) {
            return handleLambdaCall(ctx);
        }
        if (ctx.funcChain() != null) {
            FuncChainVisitor fcv = new FuncChainVisitor(this.parseContext);
            return fcv.visit(ctx.funcChain());
        }
        throw new SSParseException("Invalid base expression");
    }

    private CoreBlock handleLambdaCall(SScriptParser.BaseExprContext ctx) {
        ExpressionVisitor ev = new ExpressionVisitor(this.parseContext);
        List<CoreBlock> lb = new ArrayList<>();
        for(var ec : ctx.lambdaCall().expression()) {
            CoreBlock bb = ev.visit(ec);
            if (bb != null) {
                lb.add(bb);
            }
        }
        return new LambdaCallBlock(ctx.start, ctx.lambdaCall().id.getText(), lb);
    }
}
