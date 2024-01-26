package com.staberinde.sscript.visitor;

import com.staberinde.sscript.SScriptParser;
import com.staberinde.sscript.model.SParseContext;
import com.staberinde.sscript.program.CoreBlock;

public class ExpressionVisitor extends ContextualVisitor<CoreBlock> {
    ExpressionVisitor(final SParseContext parseContext) {
        super(parseContext);
    }

    @Override
    public CoreBlock visitExpression(SScriptParser.ExpressionContext ctx) {
        ValueExpressionVisitor vev = new ValueExpressionVisitor(this.parseContext);
        return vev.visit(ctx.valueExpression());
    }
}
