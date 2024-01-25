package com.staberinde.sscript.value;

import java.util.Objects;

public class BlockValueDouble extends NumericBlockValue<Double> {
    private static final ValueType TYPE = ValueType.DOUBLE;

    private final double value;
    private final int scale;

    public static BlockValueDouble from(final Double d) {
        return new BlockValueDouble(d, DEFAULT_FP_SCALE);
    }

    public static BlockValueDouble from(final Double d, final int scale) {
        return new BlockValueDouble(d, scale);
    }

    private BlockValueDouble(final Double d, final int scale) {
        this.value = d;
        this.scale = scale;
    }

    @Override
    public ValueType getType() {
        return TYPE;
    }

    @Override
    protected Double getValue() {
        return value;
    }

    @Override
    public BlockValue copy() {
        return BlockValueDouble.from(this.value, this.scale);
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
        if (!(obj instanceof BlockValueDouble)) {
            return false;
        }
        return this.value == ((BlockValueDouble)obj).getValue();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value, this.scale, TYPE);
    }
}
