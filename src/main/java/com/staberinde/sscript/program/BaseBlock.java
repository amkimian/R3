package com.staberinde.sscript.program;

import com.staberinde.sscript.exception.SSException;
import com.staberinde.sscript.model.BlockProgramContext;
import com.staberinde.sscript.value.BlockValue;

public interface BaseBlock extends CoreBlock {
    @Override
    default BlockValue run(final ProgramContext<BlockValue> context) {
        if (context instanceof BlockProgramContext) {
            return this.run((BlockProgramContext)context);
        } else {
            throw new SSException("Cannot run this block");
        }
    }

    BlockValue run(final BlockProgramContext context);
}
