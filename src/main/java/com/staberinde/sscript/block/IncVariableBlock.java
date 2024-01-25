package com.staberinde.sscript.block;

import com.staberinde.sscript.program.CoreBlock;
import com.staberinde.sscript.program.ProgramContext;
import com.staberinde.sscript.value.BlockValue;
import org.antlr.v4.runtime.Token;

public class IncVariableBlock extends LocationAwareBlock implements CoreBlock {
    private final String name;

    public IncVariableBlock(Token loc, String name) {
        super(loc);
        this.name = name;
    }

    @Override
    public BlockValue run(ProgramContext<BlockValue> context) {
        BlockValue value = context.getVariable(name);
        BlockValue newVal = BlockValue.from(value.asInteger() + 1);
        context.setVariable(name, newVal);
        return newVal;
    }
}
