package com.fr.swift.beans.annotation;

import java.lang.annotation.*;

/**
 * @author anner
 * @this annotation created on date 2019/8/9
 * @description
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SwiftQualilifer {
    String beanName() default "";
}
