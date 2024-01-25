package com.staberinde.sscript.value;

import com.staberinde.sscript.exception.SSException;

import java.util.ArrayList;
import java.util.List;
import java.text.ParseException;

public class BlockValueString extends AbstractBlockValue<String> {

    private static final java.text.NumberFormat NF = java.text.NumberFormat.getInstance();

        private static final ValueType TYPE = ValueType.STRING;

        private final String value;

        private NumericBlockValue<?> parsed;

        public static BlockValueString from(final String o) {
            if (o.startsWith("\"") || o.startsWith("'")) {
                return new BlockValueString(o.substring(1, o.length() - 1));
            }
            return new BlockValueString(o);
        }

        private BlockValueString(final String o) {
            this.value = o;
        }

        @Override
        public ValueType getType() {
            return TYPE;
        }

        @Override
        protected String getValue() {
            return value;
        }

    @Override
    public boolean asBoolean() {
        return Boolean.parseBoolean(this.getValue());
    }

    @Override
    public int asInteger() {
        return parseNum().asInteger();
    }

    @Override
    public long asLong() {
        return parseNum().asLong();
    }

    @Override
    public float asFloat() {
        return parseNum().asFloat();
    }

    @Override
    public double asDouble() {
        return parseNum().asDouble();
    }

    @Override
    public List<BlockValue> asList() {
        final List<BlockValue> list = new ArrayList<>();
        list.add(BlockValue.from(this.getValue()));
        return list;
    }

    ValueType numericType() {
            return parseNum().getType();
    }

    private NumericBlockValue<?> parseNum() {
        if (this.parsed == null) {
            try {
                if (value.contains(".")) {
                    if (value.contains("f")) {
                        this.parsed = BlockValueFloat.from(Float.parseFloat(this.getValue()));
                    } else {
                        this.parsed = BlockValueDouble.from(Double.parseDouble(this.getValue()));
                    }
                    this.parsed = BlockValueDouble.from(Double.parseDouble(this.getValue()));
                } else {
                    final boolean isLong = value.contains("L");
                    final long num = NF.parse(value.replace("L", "")).longValue();
                    if (isLong || num > Integer.MAX_VALUE || num < Integer.MIN_VALUE) {
                        this.parsed = BlockValueLong.from(num);
                    } else {
                        this.parsed = BlockValueInteger.from((int)num);
                    }
                }
             } catch(final ParseException | NumberFormatException e) {
                throw new SSException("Cannot parse number: " + this.getValue());
            }
        }
        return this.parsed;
    }

    @Override
    protected ValueType mostComplex(BlockValue other) {
        if (other.isNumeric()) {
            try {
                return ValueType.values()[Math.max(this.numericType().ordinal(), other.getType().ordinal())];
            } catch(final SSException e) {
                return TYPE;
            }
        } else {
            return super.mostComplex(other);
        }
    }

    @Override
    public BlockValue add(BlockValue other) {
        if (other.isNumeric()) {
            try {
                return switch (mostComplex(other)) {
                    case FLOAT -> BlockValueFloat.from(this.asFloat() + other.asFloat());
                    case DOUBLE -> BlockValueDouble.from(this.asDouble() + other.asDouble());
                    case LONG -> BlockValueLong.from(this.asLong() + other.asLong());
                    case INTEGER -> BlockValueInteger.from(this.asInteger() + other.asInteger());
                    default -> throw new SSException("Cannot add " + this.getType() + " and " + other.getType());
                };
            } catch(final SSException e) {
                throw new SSException("Cannot add " + this.getType() + " and " + other.getType());
            }
        } else {
            return super.add(other);
        }
    }

    @Override
    public BlockValue multiply(BlockValue other) {
        return switch (mostComplex(other)) {
            case FLOAT -> BlockValueFloat.from(this.asFloat() * other.asFloat());
            case DOUBLE -> BlockValueDouble.from(this.asDouble() * other.asDouble());
            case LONG -> BlockValueLong.from(this.asLong() * other.asLong());
            case INTEGER -> BlockValueInteger.from(this.asInteger() * other.asInteger());
            default -> super.multiply(other);
        };
    }

    @Override
    public BlockValue divide(BlockValue other) {
        return switch(mostComplex(other)) {
            case FLOAT -> BlockValueFloat.from(this.asFloat() / other.asFloat());
            case DOUBLE -> BlockValueDouble.from(this.asDouble() / other.asDouble());
            case LONG -> BlockValueLong.from(this.asLong() / other.asLong());
            case INTEGER -> BlockValueInteger.from(this.asInteger() / other.asInteger());
            default -> super.divide(other);
        };
    }

    @Override
    public BlockValue mod(BlockValue other) {
        return switch(mostComplex(other)) {
            case INTEGER -> BlockValueInteger.from(this.asInteger() % other.asInteger());
            case LONG -> BlockValueLong.from(this.asLong() % other.asLong());
            default -> super.mod(other);
        };
    }

    @Override
    public BlockValue power(BlockValue other) {
        return switch(mostComplex(other)) {
            case FLOAT -> BlockValueFloat.from((float)Math.pow(this.asDouble(), other.asDouble()));
            case DOUBLE -> BlockValueDouble.from(Math.pow(this.asDouble(), other.asDouble()));
            case LONG -> BlockValueLong.from((long)Math.pow(this.asLong(), other.asLong()));
            case INTEGER -> BlockValueInteger.from((int)Math.pow(this.asInteger(), other.asInteger()));
            default -> super.power(other);
        };
    }

    @Override
    public BlockValue contains(BlockValue other) {
        return BlockValueBoolean.from(other.isString() && this.value.contains(other.asString()));
    }

    @Override
        public String asString() {
            return this.getValue();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof BlockValueString)) {
                return false;
            }
            return this.value.equals(((BlockValueString)obj).getValue());
        }

        @Override
        public int hashCode() {
            return this.value.hashCode();
        }
}
