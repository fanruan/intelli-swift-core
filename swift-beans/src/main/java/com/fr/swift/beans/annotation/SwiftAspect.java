package com.fr.swift.beans.annotation;

/**
 * @author anner
 * @this annotation created on date 2019/8/13
 * @description 标注一个类是通知
 */

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SwiftAspect {
}
