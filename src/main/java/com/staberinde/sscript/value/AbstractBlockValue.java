package com.staberinde.sscript.value;

import com.staberinde.sscript.block.LambdaDef;
import com.staberinde.sscript.exception.SSException;
import com.staberinde.sscript.util.Pair;

import java.io.Serializable;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public abstract class AbstractBlockValue<T> implements BlockValue, Serializable {
    private static final String CANNOT_COMPARE = "Cannot compare these";
    private static final Set<ValueType> TYPES_WITH_PRECEDENCE = EnumSet.of(
            ValueType.BOOLEAN,
            ValueType.INTEGER,
            ValueType.LONG,
            ValueType.FLOAT,
            ValueType.DOUBLE,
            ValueType.STRING,
            ValueType.LIST,
            ValueType.SET,
            ValueType.MAP
    );

    private static final Set<ValueType> NUMERICS = EnumSet.of(
        ValueType.INTEGER,
            ValueType.LONG,
            ValueType.FLOAT,
            ValueType.DOUBLE
    );

    private static final double EPSDOUBLE = 1E-6;
    protected abstract T getValue();

    public BlockValue copy() {
        return BlockValue.from(this.getValue());
    }

    public final boolean testEquals(final Object obj) {
        if (obj == null) {
            return false;
        } else if (this == obj) {
            return true;
        } else if (!(obj instanceof final AbstractBlockValue<?> val)) {
            return false;
        } else {
            try {
                final ValueType toCompare = this.mostComplex(val);
                switch(toCompare) {
                    case BOOLEAN -> {
                        return this.asBoolean() == val.asBoolean();
                    }
                    case INTEGER -> {
                        return this.asInteger() == val.asInteger();
                    }
                    case LONG -> {
                        return this.asLong() == val.asLong();
                    }
                    case FLOAT -> {
                        return Float.compare(this.asFloat(), val.asFloat()) == 0;
                    }
                    case DOUBLE -> {
                        return Math.abs(this.asDouble() - val.asDouble()) < EPSDOUBLE;
                    }
                    case STRING -> {
                        return this.toString().equals(val.toString());
                    }
                }
            } catch(final SSException e) {

            }
            return this.getValue().equals(val.getValue());
        }
    }

    public String convertToString() {
        return this.toString();
    }

    public String toString() {
        return this.getValue().toString();
    }

    public boolean isBoolean() {
        return this.getType().equals(ValueType.BOOLEAN);
    }

    public boolean isInteger() {
        return this.getType().equals(ValueType.INTEGER);
    }

    @Override
    public boolean isDouble() {
        return this.getType().equals(ValueType.DOUBLE);
    }

    public boolean isLong() {
        return this.getType().equals(ValueType.LONG);
    }

    public boolean isFloat() {
        return this.getType().equals(ValueType.FLOAT);
    }

    @Override
    public boolean isNumeric() {
        return NUMERICS.contains(this.getType());
    }

    public boolean isString() {
        return this.getType().equals(ValueType.STRING);
    }

    public boolean isPair() {
        return this.getType().equals(ValueType.PAIR);
    }

    @Override
    public boolean isList() {
        return this.getType().equals(ValueType.LIST);
    }

    public boolean isSet() {
        return this.getType().equals(ValueType.SET);
    }

    public boolean isMap() {
        return this.getType().equals(ValueType.MAP);
    }

    public boolean isSpecial() {
        return this.getType().equals(ValueType.SPECIAL);
    }

    public boolean isJavaObj() {
        return this.getType().equals(ValueType.JAVAOBJ);
    }

    public boolean isLambda() {
        return this.getType().equals(ValueType.LAMBDA);
    }

    public boolean isNull() {
        return this.getType().equals(ValueType.NULL);
    }

    @Override
    public boolean asBoolean() {
        throw SSException.createTypeMismatchException(ValueType.BOOLEAN.toString(), getType().toString());
    }

    @Override
    public int asInteger() {
        throw SSException.createTypeMismatchException(ValueType.INTEGER.toString(), getType().toString());
    }

    @Override
    public long asLong() {
        return 0;
    }

    @Override
    public float asFloat() {
        return 0;
    }

    @Override
    public double asDouble() {
        return 0;
    }

    public String asString() {
        return "";
    }

    @Override
    public Pair<String, BlockValue> asPair() {
        return null;
    }

    public List<BlockValue> asList() {
        if (this.getType().equals(ValueType.LIST)) {
            return ((List<BlockValue>) this.getValue()).stream().map(BlockValue::from).collect(Collectors.toList());
        } else {
            final List<BlockValue> ret = new ArrayList<>();
            ret.add(BlockValue.from(this.getValue()));
            return ret;
        }
    }

    @Override
    public Set<BlockValue> asSet() {
        final Set<BlockValue> ret = new HashSet<>();
        ret.add(BlockValue.from(this.getValue()));
        return ret;
    }

    public List<BlockValue> asInternalList() {
        return null;
    }

    public List<String> asStringList() {
        if (this.getType().equals(ValueType.LIST)) {
            return ((List<BlockValue>) this.getValue()).stream().map(BlockValue::toString).collect(Collectors.toList());
        } else {
            final List<String> ret = new ArrayList<>();
            ret.add(this.toString());
            return ret;
        }
    }

    @Override
    public Map<String, BlockValue> asMap() {
        return null;
    }

    public SpecialValue asSpecial() {
        return null;
    }

    public T asObj() {
        return this.getValue();
    }

    @Override
    public LambdaDef asLambda() {
        return null;
    }

    private int typeToPrecedence() {
        if (TYPES_WITH_PRECEDENCE.contains(getType())) {
            return getType().ordinal();
        } else {
            return -1;
        }
    }

    protected ValueType mostComplex(final BlockValue other) {
        final int ordinal = Math.max(this.typeToPrecedence(), ((AbstractBlockValue<?>)other).typeToPrecedence());
        return ordinal >=0 ? ValueType.values()[ordinal] : ValueType.NULL;
    }

    @Override
    public BlockValue add(BlockValue other) {
        switch(mostComplex(other)) {
            case INTEGER -> {
                return BlockValueInteger.from(this.asInteger() + other.asInteger());
            }
            case FLOAT -> {
                return BlockValueFloat.from(this.asFloat() + other.asFloat());
            }
            case DOUBLE -> {
                return BlockValueDouble.from(this.asDouble() + other.asDouble());
            }
            case LONG -> {
                return BlockValueLong.from(this.asLong() + other.asLong());
            }
            case SET -> {
                final Set<BlockValue> mySet = this.asSet();
                mySet.addAll(other.asSet());
                return BlockValueSet.from(mySet);
            }
            case LIST -> {
                final List<BlockValue> myList = this.asList();
                myList.addAll(other.asList());
                return BlockValueList.from(myList);
            }
            default -> {
                return BlockValueString.from(this + other.toString());
            }
        }
    }

    @Override
    public BlockValue subtract(BlockValue other) {
        switch(mostComplex(other)) {
            case INTEGER -> {
                return BlockValueInteger.from(this.asInteger() - other.asInteger());
            }
            case FLOAT -> {
                return BlockValueFloat.from(this.asFloat() - other.asFloat());
            }
            case DOUBLE -> {
                return BlockValueDouble.from(this.asDouble() - other.asDouble());
            }
            case LONG -> {
                return BlockValueLong.from(this.asLong() - other.asLong());
            }
            case SET -> {
                final Set<BlockValue> mySet = this.asSet();
                mySet.removeAll(other.asSet());
                return BlockValueSet.from(mySet);
            }
            case LIST -> {
                final List<BlockValue> myList = this.asList();
                myList.removeAll(other.asList());
                return BlockValueList.from(myList);
            }
            default -> {
                throw new SSException("Cannot do this");
            }
        }
    }

    @Override
    public BlockValue multiply(BlockValue other) {
        return null;
    }

    @Override
    public BlockValue divide(BlockValue other) {
        return null;
    }

    @Override
    public BlockValue mod(BlockValue other) {
        return null;
    }

    @Override
    public BlockValue power(BlockValue other) {
        return null;
    }

    @Override
    public BlockValue merge(BlockValue other) {
        return this.add(other);
    }

    @Override
    public BlockValue contains(BlockValue other) {
        return null;
    }

    @Override
    public BlockValue doWithEach(UnaryOperator<BlockValue> action) {
        return null;
    }

    @Override
    public BlockValue logicalAnd(BlockValue other) {
        return null;
    }

    @Override
    public BlockValue logicalOr(BlockValue other) {
        return null;
    }

    @Override
    public BlockValue lessThan(BlockValue other) {
        return null;
    }

    @Override
    public BlockValue logicalNand(BlockValue other) {
        return null;
    }

    @Override
    public BlockValue greaterThan(BlockValue other) {
        return BlockValueBoolean.from(computeGreaterThan(other));
    }

    @Override
    public int compare(BlockValue other) {
        if (this.testEquals(other)) {
            return 0;
        }
        if (computeLessThan(other)) {
            return -1;
        }
        return 1;
    }

    protected boolean computeLessThan(final BlockValue other) {
        return switch(mostComplex(other)) {
            case INTEGER -> this.asInteger() < other.asInteger();
            case FLOAT -> this.asFloat() < other.asFloat();
            case DOUBLE -> this.asDouble() < other.asDouble();
            case LONG -> this.asLong() < other.asLong();
            default -> throw new SSException(CANNOT_COMPARE + this.getType() + "-" + other.getType());
        };
    }
    protected boolean computeGreaterThan(final BlockValue other) {
        return switch(mostComplex(other)) {
            case INTEGER -> this.asInteger() > other.asInteger();
            case FLOAT -> this.asFloat() > other.asFloat();
            case DOUBLE -> this.asDouble() > other.asDouble();
            case LONG -> this.asLong() > other.asLong();
            default -> throw new SSException(CANNOT_COMPARE + this.getType() + "-" + other.getType());
        };
    }
}
