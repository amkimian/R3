package com.staberinde.sscript.program;

import com.staberinde.sscript.value.BlockValue;

public class BaseScope extends Scope<BlockValue> {
    @Override
    public Scope<BlockValue> newScope() {
        return new BaseScope();
    }

    @Override
    public BlockValue wrapValue(Object o) {
        return BlockValue.from(o);
    }
}
