package com.fr.swift.rpc.annotation;

import com.fr.third.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This class created on 2018/6/6
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {

    Class<?> value();

    String version() default "";
}
