package com.fr.swift.exception;

import com.fr.swift.config.convert.ConfigAttributeConverter;

/**
 * @author anchore
 * @date 2019/8/12
 */
public class ExceptionContextConverter<T> implements ConfigAttributeConverter<T, String> {
    @Override
    public String convertToDatabaseColumn(T t) {
        return null;
    }

    @Override
    public T convertToEntityAttribute(String s) {
        return null;
    }
}