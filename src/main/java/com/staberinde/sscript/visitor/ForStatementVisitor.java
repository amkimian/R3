package com.staberinde.sscript.visitor;

import com.staberinde.sscript.SScriptParser;
import com.staberinde.sscript.block.ForBlock;
import com.staberinde.sscript.model.SParseContext;
import com.staberinde.sscript.program.CoreBlock;
import com.staberinde.sscript.value.BlockValueBoolean;

public class ForStatementVisitor extends ContextualVisitor<CoreBlock> {
    ForStatementVisitor(final SParseContext parseContext) {
        super(parseContext);
    }

    @Override
    public CoreBlock visitForStatement(SScriptParser.ForStatementContext ctx) {
        AssignmentVisitor av = new AssignmentVisitor(this.parseContext);
        ExpressionVisitor ev = new ExpressionVisitor(this.parseContext);
        final CoreBlock startBlock;
        if (ctx.assign != null) {
            startBlock = av.visitAssignment(ctx.assign);
        } else if (ctx.varAssign != null) {
            final VarStatementVisitor vv = new VarStatementVisitor(this.parseContext);
            startBlock = vv.visitVarStatement(ctx.varAssign);
        } else {
            startBlock = startCtx -> null;
        }

        final CoreBlock testBlock;
        if (ctx.test != null) {
            testBlock = ev.visitExpression(ctx.test);
        } else {
            testBlock = testCtx -> BlockValueBoolean.TRUE;
        }

        final CoreBlock loopBlock;
        if (ctx.loopChange !=null) {
            loopBlock = av.visitAssignment(ctx.loopChange);
        } else {
            loopBlock = loopCtx -> null;
        }

        BlockVisitor bv = new BlockVisitor(this.parseContext);
        CoreBlock mainBlock = bv.visitBlock(ctx.b);
        return new ForBlock(ctx.start, startBlock, testBlock, loopBlock, mainBlock);
    }
}
