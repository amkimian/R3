package com.staberinde.sscript.value;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class BlockValueList<L extends List<BlockValue>> extends AbstractBlockValue<List<BlockValue>> {

    private static final ValueType TYPE = ValueType.LIST;

    private final L value;

    public static BlockValueList from(final Object array) {
        return new BlockValueList<>(IntStream.range(0, Array.getLength(array))
                .mapToObj(i -> BlockValue.from(Array.get(array, i)))
                .collect(Collectors.toList()));
    }

    public static BlockValueList from(final Stream<?> coll) {
        return new BlockValueList<>(coll.map(BlockValue::from).collect(Collectors.toList()));
    }

    public static BlockValueList from(final Collection<?> coll) {
        return new BlockValueList<>(coll.stream().map(BlockValue::from).collect(Collectors.toList()));
    }

    private BlockValueList(final L list) {
        this.value = list;

    }

    @Override
    public ValueType getType() {
        return TYPE;
    }

    @Override
    protected L getValue() {
        return value;
    }

    @Override
    public List<BlockValue> asInternalList() {
        return this.getValue();
    }

    @Override
    public BlockValue contains(BlockValue other) {
        return BlockValueBoolean.from(this.value.stream().anyMatch(other::testEquals));
    }

    @Override
    public BlockValue doWithEach(UnaryOperator<BlockValue> action) {
        return BlockValueList.from(
                this.asList().stream()
                        .map(action)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public BlockValue merge(BlockValue other) {
        if (other.getType().equals(ValueType.LIST)) {
            final List<BlockValue> ret = this.asList();
            for(final BlockValue val : other.asList()) {
                if (!ret.contains(val)) {
                    ret.add(val);
                }
            }
            return BlockValueList.from(ret);
        } else {
            return super.merge(other);
        }
    }

    @Override
    public String toString() {
        return "[" + this.getValue().stream().map(BlockValue::toString).collect(Collectors.joining(",")) + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BlockValueList)) {
            return false;
        }
        return this.value.equals(((BlockValueList)obj).getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value, TYPE);
    }
}
