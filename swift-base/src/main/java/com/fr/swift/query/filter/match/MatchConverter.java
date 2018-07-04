package com.fr.swift.query.filter.match;

/**
 * Created by pony on 2018/5/22.
 */
public interface MatchConverter<T> {
    T convert(Object data);
}
