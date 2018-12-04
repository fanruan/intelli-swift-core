package com.fr.swift.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This class created on 2018/8/8
 *
 * @author Lucifer
 * @description Swift business service.
 * such as indexing,history,realtime,analyse,collate
 * @since Advanced FineBI 5.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SwiftService {
    String name() default "";

    boolean cluster() default false;
}
