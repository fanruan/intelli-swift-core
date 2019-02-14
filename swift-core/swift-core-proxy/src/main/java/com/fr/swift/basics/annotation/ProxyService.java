package com.fr.swift.basics.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yee
 * @date 2018/10/30
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProxyService {
    Class value();

    ServiceType type() default ServiceType.INTERNAL;

    enum ServiceType {
        INTERNAL, EXTERNAL;

        public boolean isInternal() {
            return this == INTERNAL;
        }
    }
}
