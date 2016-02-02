package com.ehanlin.hconvert.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 是否跳過這個欄位不做轉換。
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Skip {
    EffectiveScope value() default EffectiveScope.ALL;
}