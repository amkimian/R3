package com.staberinde.sscript.util;

import com.staberinde.sscript.value.BlockValue;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class RawValueConverter {
    private RawValueConverter() {

    }

    public static Map<Object, Object> convertMap(Map<String, BlockValue> input) {
        return input.entrySet().stream().collect(
                Collectors.toMap(
                        Map.Entry::getKey,
                        e -> convert(e.getValue()),
                        (e1, e2) -> e1,
                        LinkedHashMap::new)
        );
    }

    public static List<Object> convertList(List<BlockValue> input) {
        return input.stream().map(RawValueConverter::convert).collect(Collectors.toList());
    }

    private static Object convert(BlockValue value) {
        return switch(value.getType()) {
            case BOOLEAN -> value.asBoolean();
            case INTEGER -> value.asInteger();
            case LONG -> value.asLong();
            case FLOAT -> value.asFloat();
            case DOUBLE -> value.asDouble();
            case STRING -> value.asString();
            case LIST -> convertList(value.asList());
            case MAP -> convertMap(value.asMap());
            case LAMBDA -> value.asLambda();
            case JAVAOBJ -> value.asObj();
            case NULL -> "null";
            default -> value.asObj();
        };
    }

    private static BlockValue convertBack(Object value) {
        if (value instanceof Map) {
            return convertMapBack((Map<String, Object>)value);
        }
        if (value instanceof List) {
            return convertListBack((List<Object>)value);
        }
        return BlockValue.from(value);
    }

    public static BlockValue convertMapBack(Map<String, Object> input) {
        return BlockValue.from(
                (Map<String, BlockValue>) input.entrySet().stream().collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                e -> convertBack(e.getValue()),
                                (e1, e2) -> e1,
                                LinkedHashMap::new)
                ));
    }

    public static BlockValue convertListBack(List<Object> input) {
        return BlockValue.from(input.stream().map(RawValueConverter::convertBack).collect(Collectors.toList()));
    }
}
