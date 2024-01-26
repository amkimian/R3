package com.staberinde.sscript.visitor;

import com.staberinde.sscript.SScriptParser;
import com.staberinde.sscript.block.*;
import com.staberinde.sscript.exception.SSParseException;
import com.staberinde.sscript.model.SParseContext;
import com.staberinde.sscript.program.CoreBlock;
import com.staberinde.sscript.program.ProgramContext;
import com.staberinde.sscript.value.BlockValue;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class AssignmentVisitor extends ContextualVisitor<CoreBlock> {
    AssignmentVisitor(final SParseContext parseContext) {
        super(parseContext);
    }

    @Override
    public CoreBlock visitAssignment(SScriptParser.AssignmentContext ctx) {
        if (ctx.plusplus != null) {
            return new IncVariableBlock(ctx.start, ctx.plusplus.getText());
        } else if (ctx.minusminus != null) {
            return new DecVariableBlock(ctx.start, ctx.minusminus.getText());
        }
        final CoreBlock toSet;
        if (ctx.exp != null) {
            ExpressionVisitor ev = new ExpressionVisitor(this.parseContext);
            toSet = ev.visit(ctx.exp);
        } else {
            throw new SSParseException("Assignment must have an expression");
        }
        if (ctx.id != null) {
            return new VariablesSet(ctx.start, ctx.id.getText(), toSet);
        } else if (ctx.op != null) {
            return handleOpEquals(ctx, toSet);
        } else if (ctx.indexId != null) {
            return handleIndexAssign(ctx.indexId, toSet);
        } else {
            throw new SSParseException("Assignment must have an identifier");
        }
    }

    private CoreBlock handleOpEquals(final SScriptParser.AssignmentContext ctx, final CoreBlock toSet) {
        final CoreBlock getter;
        final BiConsumer<ProgramContext<BlockValue>, BlockValue> setter;
        if (ctx.indexIdentifier() != null) {
            final SScriptParser.IndexIdentifierContext idxCtx = ctx.indexIdentifier();
            final ExpressionVisitor ev = new ExpressionVisitor(this.parseContext);
            final List<CoreBlock> indices = idxCtx.index.stream().map(ev::visitExpression).collect(Collectors.toList());
            final String varName = idxCtx.id.getText();
            getter = new VariablesGet(idxCtx.start, varName, indices);
            setter = (c, v) -> {
                final List<BlockValue> idxs = indices.stream().map(b -> b.run(c)).collect(Collectors.toList());
                c.setVariableWithIndex(varName, idxs, v);
            };
        } else {
            final String varName = ctx.opequals.getText();
            getter = new VariablesGet(ctx.start, varName);
            setter = (c, v) -> c.setVariable(varName, v);
        }
        return new OperatorEqualsVariable(ctx.start, getter, ctx.op, toSet, setter);
    }

    private CoreBlock handleIndexAssign(final SScriptParser.IndexIdentifierContext idxCtx, final CoreBlock toSet) {
        final ExpressionVisitor ev = new ExpressionVisitor(this.parseContext);
        final List<CoreBlock> indices = idxCtx.index.stream().map(ev::visitExpression).collect(Collectors.toList());
        return new VariablesSet(idxCtx.start, idxCtx.id.getText(), toSet, indices);
    }
}
