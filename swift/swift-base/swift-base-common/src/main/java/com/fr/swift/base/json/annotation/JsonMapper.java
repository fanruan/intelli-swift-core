package com.fr.swift.base.json.annotation;

import com.fr.swift.base.json.mapper.BeanMapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yee
 * @date 2018-12-04
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface JsonMapper {
    Class<? extends BeanMapper> value();
}
