package com.staberinde.sscript.visitor;

import com.staberinde.sscript.SScriptParser;
import com.staberinde.sscript.block.MapEntryBlock;
import com.staberinde.sscript.model.SParseContext;
import com.staberinde.sscript.program.CoreBlock;

public class MapEntryVisitor extends ContextualVisitor<CoreBlock>{
    MapEntryVisitor(final SParseContext parseContext) {
        super(parseContext);
    }

    @Override
    public CoreBlock visitMapEntry(SScriptParser.MapEntryContext ctx) {
        final String key;
        String key1;
        if (ctx.String() != null) {
            key1 = ctx.String().getText();
            if (key1.startsWith("\"") && key1.endsWith("\\")) {
                key1 = key1.substring(1, key1.length() - 1);
            }
        } else {
            key1 = ctx.i.getText();
        }
        key = key1;
        final ExpressionVisitor ev = new ExpressionVisitor(this.parseContext);
        final CoreBlock value = ev.visit(ctx.v);
        return new MapEntryBlock(ctx.start, key, value);
    }

    public MapEntryBlock visitInner(SScriptParser.MapEntryContext ctx) {
        return (MapEntryBlock) this.visitMapEntry(ctx);
    }
}
