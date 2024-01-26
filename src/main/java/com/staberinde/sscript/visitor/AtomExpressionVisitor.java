package com.staberinde.sscript.visitor;

import com.staberinde.sscript.SScriptParser;
import com.staberinde.sscript.block.AtomListBlock;
import com.staberinde.sscript.block.AtomMapBlock;
import com.staberinde.sscript.block.AtomSetBlock;
import com.staberinde.sscript.block.VariablesGet;
import com.staberinde.sscript.model.SParseContext;
import com.staberinde.sscript.program.CoreBlock;

import java.util.List;
import java.util.stream.Collectors;

public class AtomExpressionVisitor extends ContextualVisitor<CoreBlock>{
    AtomExpressionVisitor(final SParseContext parseContext) {
        super(parseContext);
    }

    @Override
    public CoreBlock visitAtom(SScriptParser.AtomContext ctx) {
        if (ctx.indexIdentifier() != null) {
            return handleIndexAccess(ctx.indexIdentifier());
        } else if (ctx.list() != null) {
            return handleList(ctx.list());
        } else if (ctx.map() != null) {
            return handleMap(ctx.map());
        } else if (ctx.set() != null) {
            return handleSet(ctx.set());
        } else if (ctx.Identifier() != null) {
            return new VariablesGet(ctx.start, ctx.Identifier().getText());
        } else if (ctx.DottedIdentifier() != null) {
            return new VariablesGet(ctx.start, ctx.DottedIdentifier().getText());
        } else if (ctx.primitive() != null) {
            final PrimitiveVisitor pv = new PrimitiveVisitor();
            return pv.visitPrimitive(ctx.primitive());
        } else {
            throw new RuntimeException("Invalid atom expression");
        }
    }

    private CoreBlock handleIndexAccess(final SScriptParser.IndexIdentifierContext ctx) {
        final ExpressionVisitor ev = new ExpressionVisitor(this.parseContext);
        final String varName = ctx.id.getText();
        final List<CoreBlock> indices = ctx.index.stream().map(ev::visitExpression).collect(Collectors.toList());
        return new VariablesGet(ctx.start, varName, indices);
    }

    private CoreBlock handleList(final SScriptParser.ListContext ctx) {
        final AtomListBlock lb = new AtomListBlock(ctx.start);
        final ExpressionVisitor ev = new ExpressionVisitor(this.parseContext);
        ctx.e.stream().map(ev::visitExpression).forEach(lb::addValue);
        return lb;
    }

    private CoreBlock handleMap(final SScriptParser.MapContext ctx) {
        final AtomMapBlock mb = new AtomMapBlock(ctx.start);
        final MapEntryVisitor mev = new MapEntryVisitor(this.parseContext);
        ctx.e.stream().map(mev::visitInner).forEach(mb::addEntry);
        return mb;
    }

    private CoreBlock handleSet(final SScriptParser.SetContext ctx) {
        final AtomSetBlock sb = new AtomSetBlock(ctx.start);
        final ExpressionVisitor ev = new ExpressionVisitor(this.parseContext);
        ctx.e.stream().map(ev::visitExpression).forEach(sb::addEntry);
        return sb;
    }
}
