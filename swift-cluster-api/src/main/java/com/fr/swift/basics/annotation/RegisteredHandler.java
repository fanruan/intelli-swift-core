package com.fr.swift.basics.annotation;

import com.fr.swift.basics.ProcessHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This class created on 2019/1/22
 *
 * @author Lucifer
 * @description
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RegisteredHandler {

    /**
     * 标记的实例handler的key
     *
     * @return
     */
    Class<? extends ProcessHandler> value();

}
