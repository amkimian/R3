package com.staberinde.sscript.program;

import java.lang.reflect.Field;
import java.util.function.Function;

import com.staberinde.sscript.annotation.SParam;
import com.staberinde.sscript.value.BlockValue;
import com.staberinde.sscript.value.ValueType;

public class ParamInfo {
    final Field field;
    final Function<BlockValue, Object> converter;

    ParamInfo(final Field field, final SParam annotation) {
        this.field = field;
        if (BlockValue.class.isAssignableFrom(field.getType())) {
            this.converter = (v) -> v;
        } else {
            final Function<BlockValue, Object> conversionFunc = getConverter(annotation.type());
            if (conversionFunc != null && annotation.nullable()) {
                this.converter = (v) -> v.isNull() ? null : conversionFunc.apply(v);
            } else {
                this.converter = conversionFunc;
            }
        }
    }

    private Function<BlockValue, Object> getConverter(final ValueType type) {
        return switch (type) {
            case NULL -> null;
            case BOOLEAN -> BlockValue::asBoolean;
            case STRING -> BlockValue::asString;
            case INTEGER -> BlockValue::asInteger;
            case FLOAT -> BlockValue::asFloat;
            case LIST -> BlockValue::asList;
            case MAP -> BlockValue::asMap;
            case LAMBDA -> BlockValue::asLambda;
            default -> BlockValue::asObj;
        };
    }

    boolean needsProxy() {
        return this.converter != null;
    }
}
