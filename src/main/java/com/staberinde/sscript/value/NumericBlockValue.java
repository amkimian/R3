package com.staberinde.sscript.value;

import com.staberinde.sscript.exception.SSException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;

public abstract class NumericBlockValue<T extends Number> extends AbstractBlockValue<T> {
    private static final int DEFAULT_SCALE = 0;
    protected static final int DEFAULT_FP_SCALE = 2;

    public int getScale() {
        return DEFAULT_SCALE;
    }

    @Override
    public abstract boolean asBoolean();
    @Override
    public abstract int asInteger();
    @Override
    public abstract long asLong();

    @Override
    public float asFloat() {
        return this.getValue().floatValue();
    }

    @Override
    public double asDouble() {
        return this.getValue().doubleValue();
    }

    @Override
    public String asString() {
        return this.toString();
    }

    @Override
    protected ValueType mostComplex(BlockValue other) {
        if (other.isString()) {
            try {
                final ValueType numericType = ((BlockValueString) other).numericType();
                return ValueType.values()[Math.max(numericType.ordinal(), this.getType().ordinal())];
            } catch(SSException e) {
                return ValueType.STRING;
            }
        } else {
            return super.mostComplex(other);
        }
    }

    @Override
    public BlockValue multiply(BlockValue other) {
        switch(mostComplex(other)) {
            case INTEGER -> {
                return BlockValueInteger.from(this.asInteger() * other.asInteger());
            }
            case FLOAT -> {
                return BlockValueFloat.from(this.asFloat() * other.asFloat());
            }
            case DOUBLE -> {
                return BlockValueDouble.from(this.asDouble() * other.asDouble());
            }
            case LONG -> {
                return BlockValueLong.from(this.asLong() * other.asLong());
            }
            default -> {
                return BlockValueDouble.from(this.asDouble() * other.asDouble());
            }
        }
    }

    @Override
    public BlockValue divide(BlockValue other) {
        switch(mostComplex(other)) {
            case INTEGER -> {
                return BlockValueInteger.from(this.asInteger() / other.asInteger());
            }
            case FLOAT -> {
                return BlockValueFloat.from(this.asFloat() / other.asFloat());
            }
            case DOUBLE -> {
                return BlockValueDouble.from(this.asDouble() / other.asDouble());
            }
            case LONG -> {
                return BlockValueLong.from(this.asLong() / other.asLong());
            }
            default -> {
                return BlockValueDouble.from(this.asDouble() / other.asDouble());
            }
        }
    }

    @Override
    public abstract BlockValue power(BlockValue other);

    @Override
    public String convertToString() {
        return BigDecimal.valueOf(this.getValue().doubleValue()).setScale(this.getScale(), RoundingMode.HALF_UP).toString();
    }

    @Override
    public String toString() {
        return this.getValue().toString();
    }
}
