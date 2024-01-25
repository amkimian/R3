package com.staberinde.sscript.value;

import com.staberinde.sscript.block.LambdaDef;

import java.util.Objects;

public class BlockValueLambda extends AbstractBlockValue<LambdaDef> {

    private static final ValueType TYPE = ValueType.LAMBDA;

    private final LambdaDef value;

    public static BlockValueLambda from(final LambdaDef o) {
        return new BlockValueLambda(o);
    }

    private BlockValueLambda(final LambdaDef o) {
        this.value = o;
    }

    @Override
    public ValueType getType() {
        return TYPE;
    }

    @Override
    protected LambdaDef getValue() {
        return value;
    }

    @Override
    public LambdaDef asLambda() {
        return this.getValue();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BlockValueLambda)) {
            return false;
        }
        return this.value.equals(((BlockValueLambda)obj).getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value, TYPE);
    }
}
