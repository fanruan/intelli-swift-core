package com.fr.swift.beans.annotation;

import java.lang.annotation.*;

/**
 * @author anner
 * @this annotation created on date 2019/8/8
 * @description 目前只支持属性的注入
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SwiftAutoWired {
    String name() default "";
}
