package com.staberinde.sscript.visitor;

import com.staberinde.sscript.SScriptParser;
import com.staberinde.sscript.exception.SSParseException;
import com.staberinde.sscript.model.SParseContext;
import com.staberinde.sscript.program.CoreBlock;

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
        throw new SSParseException("Invalid base expression");
    }
}
