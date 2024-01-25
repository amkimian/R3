package com.staberinde.sscript.block;

import com.staberinde.sscript.program.CoreBlock;
import com.staberinde.sscript.program.ProgramContext;
import com.staberinde.sscript.value.BlockValue;
import org.antlr.v4.runtime.Token;

import java.util.List;

public class VariablesSet extends LocationAwareBlock implements CoreBlock {
    private final String varName;
    private final CoreBlock value;
    private final List<CoreBlock> indices;

    public VariablesSet(Token loc, String varName, CoreBlock value, List<CoreBlock> indices) {
        super(loc);
        this.varName = varName;
        this.value = value;
        this.indices = indices;
    }
    public VariablesSet(Token loc, String varName, CoreBlock value) {
        super(loc);
        this.varName = varName;
        this.value = value;
        this.indices = null;
    }

    @Override
    public BlockValue run(ProgramContext<BlockValue> context) {
        BlockValue v = this.value.run(context);
        if (this.indices != null) {
            final List<BlockValue> idxs = this.indices.stream().map(i->i.run(context)).toList();
            if (idxs.isEmpty()) {
                return v;
            }
            context.setVariableWithIndex(this.varName, idxs, v);
        } else {
            context.setVariable(this.varName, v);
        }
        return v;
    }
}
