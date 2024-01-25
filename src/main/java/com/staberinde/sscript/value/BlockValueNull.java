package com.staberinde.sscript.value;

import java.util.Objects;

public class BlockValueNull extends AbstractBlockValue<Void> {

    private static final ValueType TYPE = ValueType.NULL;
    private static final BlockValueNull INSTANCE = new BlockValueNull();

    public static BlockValueNull getInstance() {
        return INSTANCE;
    }

    private BlockValueNull() {

    }

    @Override
    public ValueType getType() {
        return TYPE;
    }

    @Override
    protected Void getValue() {
        return null;
    }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public String convertToString() {
        return "null";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        return INSTANCE == obj;
    }

    @Override
    public int hashCode() {
        return Objects.hash(null, TYPE);
    }
}
