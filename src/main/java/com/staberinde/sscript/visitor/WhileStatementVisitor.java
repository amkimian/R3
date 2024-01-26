package com.staberinde.sscript.visitor;

import com.staberinde.sscript.SScriptParser;
import com.staberinde.sscript.block.WhileBlock;
import com.staberinde.sscript.model.SParseContext;
import com.staberinde.sscript.program.CoreBlock;

public class WhileStatementVisitor extends ContextualVisitor<CoreBlock>{
    WhileStatementVisitor(final SParseContext parseContext) {
        super(parseContext);
    }

    @Override
    public CoreBlock visitWhileStatement(SScriptParser.WhileStatementContext ctx) {
        ExpressionVisitor ev = new ExpressionVisitor(this.parseContext);
        BlockVisitor blockVisitor = new BlockVisitor(this.parseContext);
        CoreBlock testExp = ev.visitExpression(ctx.test);
        CoreBlock loopBlock = blockVisitor.visitBlock(ctx.block());
        return new WhileBlock(ctx.start, testExp, loopBlock);
    }
}
