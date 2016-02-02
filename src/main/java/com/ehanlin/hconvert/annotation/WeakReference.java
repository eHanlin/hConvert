package com.ehanlin.hconvert.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用來標示弱參照的欄位。
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface WeakReference {
    WeakReferencePolicy value() default WeakReferencePolicy.MEDIATE;
}
