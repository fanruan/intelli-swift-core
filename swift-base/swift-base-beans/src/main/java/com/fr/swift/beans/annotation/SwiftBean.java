package com.fr.swift.beans.annotation;

import com.fr.swift.util.Strings;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This class created on 2018/11/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
// TODO: 2019/1/21 要加个懒加载
public @interface SwiftBean {
    String name() default "";

    boolean autowire() default false;

    String initMethod() default "";

    String destroyMethod() default "(inferred)";
}
