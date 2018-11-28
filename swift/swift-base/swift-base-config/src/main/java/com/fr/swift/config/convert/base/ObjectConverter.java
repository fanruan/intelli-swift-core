package com.fr.swift.config.convert.base;

/**
 * @author yee
 * @date 2018-11-27
 */
public interface ObjectConverter<From, To> {
    From convertSrcObject(To to);

    To convertDestObject(From from);
}
