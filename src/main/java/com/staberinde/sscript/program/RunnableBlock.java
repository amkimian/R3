package com.staberinde.sscript.program;

import com.staberinde.sscript.value.BlockValue;

public interface RunnableBlock<V extends BlockValue> extends PureBlock {
    V run(final ProgramContext<V> context);
}
