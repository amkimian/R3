package com.staberinde.sscript.value;

import com.staberinde.sscript.util.Pair;

import java.util.Objects;

public class BlockValuePair extends AbstractBlockValue<Pair<String, BlockValue>> {
    private static final ValueType TYPE = ValueType.PAIR;

    private final Pair<String, BlockValue> value;

    public static BlockValuePair from(final Pair<String, BlockValue> pair) {
        return new BlockValuePair(pair);
    }

    private BlockValuePair(final Pair<String, BlockValue> pair) {
        this.value = pair;
    }

    @Override
    public ValueType getType() {
        return TYPE;
    }

    @Override
    protected Pair<String, BlockValue> getValue() {
        return value;
    }

    @Override
    public Pair<String, BlockValue> asPair() {
        return getValue();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BlockValuePair)) {
            return false;
        }
        return this.value.equals(((BlockValuePair)obj).getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value, TYPE);
    }
}
