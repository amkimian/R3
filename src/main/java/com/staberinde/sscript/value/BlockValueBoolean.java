package com.staberinde.sscript.value;

import java.util.Objects;

public class BlockValueBoolean extends AbstractBlockValue<Boolean> {
    public static final BlockValueBoolean TRUE = BlockValueBoolean.from(true);
    public static final BlockValueBoolean FALSE = BlockValueBoolean.from(false);

    private static final ValueType TYPE = ValueType.BOOLEAN;

    private final Boolean value;

    public static BlockValueBoolean from(final Boolean o) {
        return new BlockValueBoolean(o);
    }

    private BlockValueBoolean(final Boolean o) {
        this.value = o;
    }

    @Override
    public ValueType getType() {
        return TYPE;
    }

    @Override
    protected Boolean getValue() {
        return value;
    }

    @Override
    public boolean asBoolean() {
        return this.getValue();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BlockValueBoolean)) {
            return false;
        }
        return this.value.equals(((BlockValueBoolean)obj).getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value, TYPE);
    }
}
