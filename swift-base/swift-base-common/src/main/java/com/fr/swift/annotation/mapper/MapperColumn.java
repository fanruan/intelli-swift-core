package com.fr.swift.annotation.mapper;

import com.fr.swift.util.Strings;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lucifer
 * @date 2020/3/13
 * @description
 * @since swift 1.1
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MapperColumn {

    String mapperColumn() default Strings.EMPTY;
}
