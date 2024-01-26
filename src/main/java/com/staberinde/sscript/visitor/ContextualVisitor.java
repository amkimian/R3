package com.staberinde.sscript.visitor;

import com.staberinde.sscript.SScriptBaseVisitor;
import com.staberinde.sscript.model.SParseContext;

public abstract class ContextualVisitor<T> extends SScriptBaseVisitor<T> {
    protected final SParseContext parseContext;

    ContextualVisitor(final SParseContext parseContext) {
        this.parseContext = parseContext;
    }
}
