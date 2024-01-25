package com.staberinde.sscript.value;

import com.staberinde.sscript.exception.SSException;

import java.util.*;
import java.util.stream.Collectors;

public class BlockValueMap extends AbstractBlockValue<Map<String, BlockValue>> {

    private static final ValueType TYPE = ValueType.MAP;

    private final Map<String, BlockValue> value;

    public static BlockValueMap from(final Map<?, ?> map) {
        Map<String, BlockValue> blockValueMap = new LinkedHashMap<>();
        map.forEach((key, value) -> blockValueMap.put(String.valueOf(key), BlockValue.from(value)));
        return new BlockValueMap(blockValueMap);
    }

    private BlockValueMap(final Map<String, BlockValue> map) {
        this.value = map;
    }

    @Override
    public ValueType getType() {
        return TYPE;
    }

    @Override
    protected Map<String, BlockValue> getValue() {
        return value;
    }

    @Override
    public Map<String, BlockValue> asMap() {
        return this.getValue();
    }

    @Override
    public List<BlockValue> asList() {
        return this.getValue().keySet().stream().map(BlockValue::from).collect(Collectors.toList());
    }

    @Override
    public BlockValue contains(BlockValue other) {
        return BlockValueBoolean.from(other.isString() && this.getValue().containsKey(other.asString()));
    }

    @Override
    public BlockValue merge(BlockValue other) {
        if (other.getType().equals(ValueType.MAP)) {
            final Map<String, BlockValue> ret = new HashMap<>(this.getValue());
            other.asMap().forEach((key, val) -> ret.merge(key, val, BlockValue::merge));
            return BlockValueMap.from(ret);
        }
        throw new SSException("Cannot merge these things");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BlockValueMap)) {
            return false;
        }
        return this.value.equals(((BlockValueMap)obj).getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value, TYPE);
    }
}
