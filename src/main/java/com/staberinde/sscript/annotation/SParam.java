package com.staberinde.sscript.annotation;

import com.staberinde.sscript.value.ValueType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)

public @interface SParam {
    int value();
    String name() default "";

    ValueType type() default ValueType.NULL;

    boolean nullable() default false;
}
