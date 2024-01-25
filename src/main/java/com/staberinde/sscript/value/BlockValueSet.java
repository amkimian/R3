package com.staberinde.sscript.value;

import com.staberinde.sscript.exception.SSException;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class BlockValueSet extends AbstractBlockValue<Set<BlockValue>> {
    private static final ValueType TYPE = ValueType.SET;

    private final Set<BlockValue> value;

    public static BlockValueSet from(final Set<?> set) {
        Set<BlockValue> blockValueSet = new HashSet<>();
        set.forEach(value -> blockValueSet.add(BlockValue.from(value)));
        return new BlockValueSet(blockValueSet);
    }

    private BlockValueSet(final Set<BlockValue> set) {
        this.value = set;
    }

    @Override
    public ValueType getType() {
        return TYPE;
    }

    @Override
    protected Set<BlockValue> getValue() {
        return value;
    }

    @Override
    public Set<BlockValue> asSet() {
        return this.getValue();
    }

    @Override
    public List<BlockValue> asList() {
        return this.getValue().stream().map(BlockValue::from).collect(Collectors.toList());
    }

    @Override
    public BlockValue contains(BlockValue other) {
        return BlockValueBoolean.from(other.isString() && this.getValue().contains(other));
    }

    @Override
    public BlockValue merge(BlockValue other) {
        if (other.getType().equals(ValueType.SET)) {
            final Set<BlockValue> set = new HashSet<>(this.getValue());
            set.addAll(other.asSet());
            return BlockValueSet.from(set);
        }
        throw new SSException("Cannot do this");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BlockValueSet)) {
            return false;
        }
        return this.value == ((BlockValueSet)obj).getValue();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value, TYPE);
    }
}
