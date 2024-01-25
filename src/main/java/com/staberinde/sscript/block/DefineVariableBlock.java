package com.staberinde.sscript.block;

import com.staberinde.sscript.program.CoreBlock;
import com.staberinde.sscript.program.ProgramContext;
import com.staberinde.sscript.value.BlockValue;
import com.staberinde.sscript.value.BlockValueNull;
import org.antlr.v4.runtime.Token;

public class DefineVariableBlock extends LocationAwareBlock implements CoreBlock {
    public DefineVariableBlock(Token loc, String variableName) {
        this(loc, variableName, null);
    }

    public DefineVariableBlock(Token loc, String variableName, VariablesSet setter) {
        super(loc);
        this.variableName = variableName;
        this.setter = setter;
    }

    private final String variableName;
    private final VariablesSet setter;

    @Override
    public BlockValue run(ProgramContext<BlockValue> context) {
        context.defineVariable(this.variableName);
        if (this.setter != null) {
            this.setter.run(context);
        }
        return BlockValueNull.getInstance();
    }
}
