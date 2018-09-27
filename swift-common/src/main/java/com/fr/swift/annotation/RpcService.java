package com.fr.swift.annotation;

import com.fr.third.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This class created on 2018/6/6
 *
 * @author Lucifer
 * @description Allow services to call RPC
 * @since Advanced FineBI 5.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {

    Class<?> value();

    RpcServiceType type() default RpcServiceType.INTERNAL;

    enum RpcServiceType {
        //
        INTERNAL, EXTERNAL
    }
}
