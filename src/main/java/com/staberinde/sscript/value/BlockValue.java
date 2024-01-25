package com.staberinde.sscript.value;

import com.staberinde.sscript.block.LambdaDef;
import com.staberinde.sscript.util.Pair;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public interface BlockValue {
    ValueType getType();

    BlockValue copy();

    boolean testEquals(Object obj);

    boolean equals(Object obj);

    int hashCode();

    String convertToString();

    boolean isBoolean();
    boolean isInteger();
    boolean isLong();
    boolean isFloat();
    boolean isDouble();
    boolean isNumeric();
    boolean isString();
    boolean isPair();
    boolean isList();
    boolean isMap();
    boolean isSet();
    boolean isSpecial();
    boolean isLambda();
    boolean isJavaObj();
    boolean isNull();

    boolean asBoolean();
    int asInteger();
    long asLong();
    float asFloat();
    double asDouble();
    String asString();
    Pair<String, BlockValue> asPair();
    List<BlockValue> asList();
    List<BlockValue> asInternalList();
    Map<String, BlockValue> asMap();
    Set<BlockValue> asSet();
    SpecialValue asSpecial();
    LambdaDef asLambda();
    Object asObj();

    BlockValue add(final BlockValue other);
    BlockValue subtract(final BlockValue other);
    BlockValue multiply(final BlockValue other);
    BlockValue divide(final BlockValue other);
    BlockValue mod(final BlockValue other);
    BlockValue power(final BlockValue other);
    BlockValue logicalAnd(final BlockValue other);
    BlockValue logicalNand(final BlockValue other);
    BlockValue logicalOr(final BlockValue other);
    BlockValue lessThan(final BlockValue other);
    BlockValue greaterThan(final BlockValue other);
    BlockValue merge(final BlockValue other);
    BlockValue doWithEach(final UnaryOperator<BlockValue> action);
    BlockValue contains(final BlockValue other);

    static BlockValue from(final Object obj) {
        if (obj == null) {
            return BlockValueNull.getInstance();
        } else if (obj instanceof BlockValue) {
            return ((BlockValue)obj).copy();
        } else if (obj.getClass().isArray()) {
            return BlockValueList.from(obj);
        } else if (obj instanceof Boolean) {
            return BlockValueBoolean.from((boolean) obj);
        } else if (obj instanceof String) {
            return BlockValueString.from((String) obj);
        } else if (obj instanceof Number) {
            return BlockValue.from((Number) obj);
        } else if (obj instanceof Pair<?, ?>) {
            return BlockValuePair.from(Pair.of(((Pair<?,?>) obj).getFirstValue().toString(), BlockValue.from(((Pair<?,?>)obj).getSecondValue())));
        } else if (obj instanceof Set<?>) {
            return BlockValueSet.from((Set<?>)obj);
        } else if (obj instanceof Collection<?>) {
            return BlockValueList.from((Collection<?>)obj);
        } else if (obj instanceof Stream<?>) {
            return BlockValueList.from((Stream<?>)obj);
        } else if (obj instanceof Map<?, ?>) {
            return BlockValueMap.from((Map<?, ?>)obj);
        } else if (obj instanceof SpecialValue) {
            return BlockValueSpecial.from((SpecialValue)obj);
        } else if (obj instanceof LambdaDef) {
            return BlockValueLambda.from((LambdaDef)obj);
        }
        return BlockValueJavaObj.from(obj);
    }

    static BlockValue from(final Number num) {
        if (num == null) {
            return BlockValueNull.getInstance();
        } else if (num instanceof Integer || num instanceof Short) {
            return BlockValueInteger.from(num.intValue());
        } else if (num instanceof Long) {
            return BlockValueLong.from(num.longValue());
        } else if (num instanceof Float) {
            return BlockValueFloat.from(num.floatValue());
        } else {
            return BlockValueDouble.from(num.doubleValue());
        }
    }

    int compare(BlockValue value);
}
