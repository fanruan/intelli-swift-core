package com.fr.swift.exception;

import com.fr.swift.config.convert.ConfigAttributeConverter;

/**
 * @author Marvin
 * @date 8/20/2019
 * @description
 * @since swift 1.1
 */
public class ExceptionInfoTypeConverter implements ConfigAttributeConverter<ExceptionInfo.Type, String> {
    @Override
    public String convertToDatabaseColumn(ExceptionInfo.Type type) {
        //Todo
        return null;
    }

    @Override
    public ExceptionInfo.Type convertToEntityAttribute(String s) {
        //Todo
        return null;
    }
}
