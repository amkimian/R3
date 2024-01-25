package com.staberinde.sscript.model;

import com.staberinde.sscript.program.BaseScope;
import com.staberinde.sscript.program.ProgramContext;
import com.staberinde.sscript.value.BlockValue;

import java.util.Map;

public class BlockProgramContext extends ProgramContext<BlockValue> {
    public BlockProgramContext() {
        this(null);
    }

    public BlockProgramContext(final Map<String, ?> variables) {
        super(new BaseScope());
        if (variables != null) {
            variables.forEach((name, value) -> {
                if (value != null) {
                    this.currentScope.setVariable(name, BlockValue.from(value));
                }
            });
        }
    }

    public static BlockProgramContext createFromParameters(final Map<String, ?> parameters) {
        return new BlockProgramContext(parameters);
    }


}
