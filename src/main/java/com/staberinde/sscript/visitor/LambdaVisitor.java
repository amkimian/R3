package com.staberinde.sscript.visitor;

import com.staberinde.sscript.SScriptParser;
import com.staberinde.sscript.block.BlockStatementList;
import com.staberinde.sscript.block.LambdaDefBlock;
import com.staberinde.sscript.model.SParseContext;
import com.staberinde.sscript.program.CoreBlock;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

public class LambdaVisitor extends ContextualVisitor<CoreBlock> {
    LambdaVisitor(SParseContext context) {
        super(context);
    }

    @Override
    public CoreBlock visitLambdaExpression(SScriptParser.LambdaExpressionContext ctx) {
        final List<String> varNames = new ArrayList<>();
        for(Token t : ctx.id) {
            varNames.add(t.getText());
        }
        final ExpressionVisitor expressionVisitor = new ExpressionVisitor(this.parseContext);
        final BlockStatementList bsl;
        if (ctx.b != null) {
            final BlockVisitor blockVisitor = new BlockVisitor(this.parseContext);
            bsl = blockVisitor.visitBlock(ctx.b);
        } else {
            bsl = new BlockStatementList();
            bsl.addBlock(expressionVisitor.visitExpression(ctx.exp));
        }
        return new LambdaDefBlock(ctx.start, varNames, bsl);
    }
}
