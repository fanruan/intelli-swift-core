package com.fr.swift.exception;

import com.fr.swift.config.convert.ConfigAttributeConverter;

/**
 * @author anchore
 * @date 2019/8/12
 */
public class ExceptionContextConverter implements ConfigAttributeConverter<ExceptionContext, String> {
    @Override
    public String convertToDatabaseColumn(ExceptionContext t) {
        //Todo
        return null;
    }

    @Override
    public ExceptionContext convertToEntityAttribute(String s) {
        //Todo
        return null;
    }
}