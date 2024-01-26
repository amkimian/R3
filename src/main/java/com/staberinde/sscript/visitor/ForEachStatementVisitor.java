package com.staberinde.sscript.visitor;

import com.staberinde.sscript.SScriptParser;
import com.staberinde.sscript.block.ForEachBlock;
import com.staberinde.sscript.model.SParseContext;
import com.staberinde.sscript.program.CoreBlock;

public class ForEachStatementVisitor extends ContextualVisitor<CoreBlock>{
    ForEachStatementVisitor(final SParseContext parseContext) {
        super(parseContext);
    }

    @Override
    public CoreBlock visitForeachStatement(SScriptParser.ForeachStatementContext ctx) {
        ExpressionVisitor ev = new ExpressionVisitor(this.parseContext);
        CoreBlock listBlock = ev.visitExpression(ctx.listExp);
        BlockVisitor bv = new BlockVisitor(this.parseContext.newChild(false));
        CoreBlock loopBlock = bv.visitBlock(ctx.block());
        return new ForEachBlock(ctx.start, ctx.id.getText(), listBlock, loopBlock);
    }
}
