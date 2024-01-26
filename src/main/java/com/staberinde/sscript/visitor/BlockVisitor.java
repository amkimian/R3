package com.staberinde.sscript.visitor;

import com.staberinde.sscript.SScriptParser;
import com.staberinde.sscript.block.BlockStatementList;
import com.staberinde.sscript.model.SParseContext;
import com.staberinde.sscript.program.CoreBlock;

public class BlockVisitor extends ContextualVisitor<BlockStatementList> {
    public BlockVisitor(final SParseContext parseContext) {
        super(parseContext);
    }

    @Override
    public BlockStatementList visitBlock(SScriptParser.BlockContext ctx) {
        final BlockStatementList stmts = new BlockStatementList();
        final StatementVisitor sv = new StatementVisitor(this.parseContext);
        ctx.statement().forEach(stmt -> {
            final CoreBlock block = sv.visitStatement(stmt);
            if (block != null) {
                stmts.addBlock(block);
            }
        });
        return stmts;
    }
}
