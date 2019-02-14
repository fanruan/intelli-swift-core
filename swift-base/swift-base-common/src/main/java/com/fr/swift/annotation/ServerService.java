package com.fr.swift.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This class created on 2018/8/8
 *
 * @author Lucifer
 * @description Swift Communications, etc.service.
 * such as http,rpc
 * @since Advanced FineBI 5.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ServerService {
    String name() default "";
}
