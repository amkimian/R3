package com.staberinde.sscript.visitor;

import com.staberinde.sscript.SScriptParser;
import com.staberinde.sscript.model.NamedParameter;
import com.staberinde.sscript.model.SParseContext;
import com.staberinde.sscript.program.CoreBlock;

public class NamedParameterVisitor extends ContextualVisitor<NamedParameter> {
    NamedParameterVisitor(final SParseContext parseContext) {
        super(parseContext);
    }

    @Override
    public NamedParameter visitNamedParameter(SScriptParser.NamedParameterContext ctx) {
        CoreBlock mainExp;
        if (ctx.exp != null) {
            final ExpressionVisitor ev = new ExpressionVisitor(this.parseContext);
            mainExp = ev.visit(ctx.exp);
        } else {
            final LambdaVisitor lv = new LambdaVisitor(this.parseContext);
            mainExp = lv.visit(ctx.lambdaExpression());
        }
        return ctx.name == null ? new NamedParameter(mainExp) : new NamedParameter(ctx.name.getText(), mainExp);
    }
}
