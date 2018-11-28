package com.fr.swift.config.convert.hibernate;

/**
 * @author yee
 * @date 2018-11-27
 */
public interface ConfigAttributeConverter<From, To> {
    To convertToDatabaseColumn(From from);

    From convertToEntityAttribute(To s);
}
