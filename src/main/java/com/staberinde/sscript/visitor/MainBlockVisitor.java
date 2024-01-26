package com.staberinde.sscript.visitor;

import com.staberinde.sscript.SScriptParser;
import com.staberinde.sscript.block.BlockStatementList;
import com.staberinde.sscript.model.BlockProgram;
import com.staberinde.sscript.model.SParseContext;

import java.util.function.Supplier;

public class MainBlockVisitor extends ContextualVisitor<Supplier<BlockProgram>> {
    private final boolean lazyResolve;

    private final Supplier<BlockProgram> programRef;

    public MainBlockVisitor(final SParseContext context, final boolean lazyResolve) {
        super(context);
        this.lazyResolve = lazyResolve;
        this.programRef = context.programReference();
    }

    @Override
    public Supplier<BlockProgram> visitMainBlock(SScriptParser.MainBlockContext ctx) {
        final BlockVisitor bv = new BlockVisitor(this.parseContext);
        final BlockStatementList stmts = bv.visit(ctx.main);
        if (!lazyResolve) {
            this.parseContext.finalizeProgram(stmts);
            return programRef;
        } else {
            return () -> {
                if (!this.parseContext.isFinalized()) {
                    this.parseContext.finalizeProgram(stmts);
                }
                return programRef.get();
            };
        }
    }
}
