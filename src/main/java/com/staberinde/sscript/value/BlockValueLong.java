package com.staberinde.sscript.value;

import java.util.Objects;

public class BlockValueLong extends NumericBlockValue<Long> {
    private static final ValueType TYPE = ValueType.LONG;

    private final long value;

    public static BlockValueLong from(final Long d) {
        return new BlockValueLong(d);
    }

    private BlockValueLong(final long d) {
        this.value = d;
    }

    @Override
    public ValueType getType() {
        return TYPE;
    }

    @Override
    protected Long getValue() {
        return value;
    }

    @Override
    public BlockValue copy() {
        return BlockValueLong.from(this.value);
    }

    @Override
    public boolean asBoolean() {
        return this.getValue() >= 1;
    }

    @Override
    public int asInteger() {
        return Math.toIntExact(value);
    }

    @Override
    public long asLong() {
        return this.getValue();
    }

    @Override
    public BlockValue mod(BlockValue other) {
        final ValueType mostComp = mostComplex(other);
        if (mostComp.equals(ValueType.INTEGER) || mostComp.equals(ValueType.LONG)) {
            return BlockValueLong.from(this.getValue() % other.asLong());
        } else {
            return super.mod(other);
        }
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
        if (!(obj instanceof BlockValueLong)) {
            return false;
        }
        return this.value == ((BlockValueLong)obj).getValue();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value, TYPE);
    }
}
