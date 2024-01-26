package com.staberinde.sscript.model;

import com.staberinde.sscript.block.BlockStatementList;
import com.staberinde.sscript.exception.SSException;
import com.staberinde.sscript.program.ProgramContext;
import com.staberinde.sscript.value.BlockValue;

import java.util.List;

public class BlockProgram {
    private final BlockStatementList statements;
    private final List<String> requiredParams;
    public String toString() {
        return "BlockProgram [ " + statements + " ]";
    }

    public BlockProgram(final BlockStatementList statements, final List<String> requiredParams) {
        this.statements = statements;
        this.requiredParams = requiredParams;
    }

    public <C extends ProgramContext<BlockValue>> BlockValue run(C ctx) {
        requiredParams.forEach(param -> {
            if (!ctx.isDefined(param)) {
                throw new SSException("Missing required parameter: " + param);
            }
        });
        return statements.run(ctx);
    }

    public BlockStatementList getBlock() {
        return statements;
    }
}
