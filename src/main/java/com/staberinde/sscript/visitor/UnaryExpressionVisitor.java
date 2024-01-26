package com.staberinde.sscript.visitor;

import com.staberinde.sscript.SScriptLexer;
import com.staberinde.sscript.SScriptParser;
import com.staberinde.sscript.block.UnaryMinusBlock;
import com.staberinde.sscript.block.UnaryNegBlock;
import com.staberinde.sscript.model.SParseContext;
import com.staberinde.sscript.program.CoreBlock;

public class UnaryExpressionVisitor extends ContextualVisitor<CoreBlock> {
    UnaryExpressionVisitor(final SParseContext parseContext) {
        super(parseContext);
    }

    @Override
    public CoreBlock visitUnaryExpr(SScriptParser.UnaryExprContext ctx) {
        if (ctx.op != null) {
            final CoreBlock arg;
            if (ctx.expression() != null) {
                final ExpressionVisitor ev = new ExpressionVisitor(this.parseContext);
                arg = ev.visit(ctx.expression());
            } else {
                final AtomExpressionVisitor aev = new AtomExpressionVisitor(this.parseContext);
                arg = aev.visit(ctx.atom());
            }
            if (ctx.op.getType() == SScriptLexer.MINUS) {
                return new UnaryMinusBlock(ctx.start, arg);
            } else {
                return new UnaryNegBlock(ctx.start, arg, ctx.op.getType() == SScriptLexer.NOTEACH);
            }
        } else {
            final AtomExpressionVisitor aev = new AtomExpressionVisitor(this.parseContext);
            return aev.visit(ctx.atom());
        }
    }
}
