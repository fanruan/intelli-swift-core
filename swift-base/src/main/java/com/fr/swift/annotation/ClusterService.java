package com.fr.swift.annotation;

import com.fr.third.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This class created on 2018/8/17
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface ClusterService {
    String initMethod();
}
