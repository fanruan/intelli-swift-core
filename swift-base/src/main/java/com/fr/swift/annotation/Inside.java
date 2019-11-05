package com.fr.swift.annotation;

/**
 * This class created on 2019/5/28
 *
 * @author Lucifer
 * @description 仅供自己测试使用，带了这个注解的类和方法，在review的时候可以忽略
 */

public @interface Inside {
    /**
     * 被Test的类数组，可以只写一个
     */
    @Inside(value = {Inside.class, String.class}, function = "value")
    Class[] value() default {Inside.class};

    /**
     * 测试的方法名称
     */
    @Inside(value = Inside.class, function = "function")
    String[] function() default "";
}