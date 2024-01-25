package com.staberinde.sscript.value;

import java.util.Objects;

public class BlockValueFloat extends NumericBlockValue<Float> {
    private static final ValueType TYPE = ValueType.FLOAT;

    private final float value;
    private final int scale;

    public static BlockValueFloat from(final Float d) {
        return new BlockValueFloat(d, DEFAULT_FP_SCALE);
    }

    public static BlockValueFloat from(final Float d, final int scale) {
        return new BlockValueFloat(d, scale);
    }

    private BlockValueFloat(final Float d, final int scale) {
        this.value = d;
        this.scale = scale;
    }

    @Override
    public ValueType getType() {
        return TYPE;
    }

    @Override
    protected Float getValue() {
        return value;
    }

    @Override
    public BlockValue copy() {
        return BlockValueFloat.from(this.value, this.scale);
    }

    @Override
    public int getScale() {
        return scale;
    }

    @Override
    public boolean asBoolean() {
        return this.getValue() >= 1.0;
    }

    @Override
    public int asInteger() {
        return Math.toIntExact(this.asLong());
    }

    @Override
    public long asLong() {
        return Math.round(this.getValue());
    }

    @Override
    public BlockValue power(BlockValue other) {
        return BlockValue.from(Math.pow(this.asDouble(), other.asDouble()));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BlockValueFloat)) {
            return false;
        }
        return this.value == ((BlockValueFloat)obj).getValue();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value, this.scale, TYPE);
    }
}
