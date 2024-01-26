package com.staberinde.sscript.visitor;

import com.staberinde.sscript.SScriptParser;
import com.staberinde.sscript.block.IfBlock;
import com.staberinde.sscript.model.SParseContext;
import com.staberinde.sscript.program.CoreBlock;

import java.util.List;

public class IfStatementVisitor extends ContextualVisitor<CoreBlock>{
    IfStatementVisitor(final SParseContext parseContext) {
        super(parseContext);
    }

    @Override
    public CoreBlock visitIfStatement(SScriptParser.IfStatementContext ctx) {
        ExpressionVisitor ev = new ExpressionVisitor(this.parseContext);
        CoreBlock testExp = ev.visitExpression(ctx.ifTrue.test);
        BlockVisitor bv = new BlockVisitor(this.parseContext);
        CoreBlock trueBlock = bv.visitBlock(ctx.ifTrue.xb);
        CoreBlock elseBlock = ctx.el != null ? bv.visitBlock(ctx.el) : null;
        elseBlock = parseElifs(ctx.elifs, elseBlock);
        return new IfBlock(ctx.start, testExp, trueBlock, elseBlock);
    }

    private CoreBlock parseElifs(final List<SScriptParser.TestAndBlockContext> elifs, CoreBlock elseBlock) {
        if (elifs.isEmpty()) {
            return elseBlock;
        } else {
            final SScriptParser.TestAndBlockContext next = elifs.removeFirst();
            final ExpressionVisitor ev = new ExpressionVisitor(this.parseContext);
            final BlockVisitor bv = new BlockVisitor(this.parseContext);
            return new IfBlock(next.start, ev.visitExpression(next.test), bv.visitBlock(next.xb), parseElifs(elifs, elseBlock));
        }
    }
}
