package com.fr.swift.query.filter.match;

/**
 * Created by pony on 2018/5/22.
 */
public class ToStringConverter implements MatchConverter<String> {
    @Override
    public String convert(Object data) {
        return data == null ? null : data.toString();
    }
}
