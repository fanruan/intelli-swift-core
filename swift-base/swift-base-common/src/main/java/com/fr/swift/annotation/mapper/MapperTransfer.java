package com.fr.swift.annotation.mapper;

import com.fr.swift.mapper.AbstractMapperTransferFunction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Heng.J
 * @date 2020/6/17
 * @description
 * @since swift 1.1
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MapperTransfer {
    Class<? extends AbstractMapperTransferFunction> using();
}
