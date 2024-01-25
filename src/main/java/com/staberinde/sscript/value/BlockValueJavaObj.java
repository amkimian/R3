package com.staberinde.sscript.value;

import com.staberinde.sscript.exception.SSException;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

public class BlockValueJavaObj extends AbstractBlockValue<Object> {
    private static final ValueType TYPE = ValueType.JAVAOBJ;

    private final Object value;

    public static BlockValueJavaObj from(final Object obj) {
        return new BlockValueJavaObj(obj);
    }

    private BlockValueJavaObj(final Object obj) {
        this.value = obj;
    }

    @Override
    public ValueType getType() {
        return TYPE;
    }

    @Override
    protected Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Optional.ofNullable(this.getValue()).map(Object::toString).orElse("null");
    }

    public BlockValue getJavaField(String fieldName) {
        final Object objVal = this.getValue();
        for(final Method method : objVal.getClass().getMethods()) {
            if (method.getName().startsWith("get")
                && method.getName().length() == (fieldName.length()+3)
                && method.getName().toLowerCase().endsWith(fieldName.toLowerCase())) {
                try {
                    Object v = method.invoke(objVal);
                    return BlockValue.from(v);
                } catch(Exception e) {
                    throw new SSException("Failed to access java bean " + e.getMessage());
                }
            }
        }
        throw new SSException("Cannot find field name " + fieldName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BlockValueJavaObj)) {
            return false;
        }
        return this.value.equals(((BlockValueJavaObj)obj).value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value, TYPE);
    }
}
