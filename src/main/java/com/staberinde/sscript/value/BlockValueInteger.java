package com.staberinde.sscript.value;

import java.util.Objects;

public class BlockValueInteger extends NumericBlockValue<Integer> {
    private static final ValueType TYPE = ValueType.INTEGER;

    private final int value;

    public static BlockValueInteger from(final Integer d) {
        return new BlockValueInteger(d);
    }

    private BlockValueInteger(final int d) {
        this.value = d;
    }

    @Override
    public ValueType getType() {
        return TYPE;
    }

    @Override
    protected Integer getValue() {
        return value;
    }

    @Override
    public BlockValue copy() {
        return BlockValueInteger.from(this.value);
    }

    @Override
    public boolean asBoolean() {
        return this.getValue() >= 1;
    }

    @Override
    public int asInteger() {
        return value;
    }

    @Override
    public long asLong() {
        return this.getValue();
    }

    @Override
    public BlockValue mod(BlockValue other) {
        final ValueType mostComp = mostComplex(other);
        if (mostComp.equals(ValueType.INTEGER) || mostComp.equals(ValueType.LONG)) {
            return BlockValueInteger.from(this.getValue() % other.asInteger());
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
        if (!(obj instanceof BlockValueInteger)) {
            return false;
        }
        return this.value == ((BlockValueInteger)obj).getValue();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value, TYPE);
    }
}
