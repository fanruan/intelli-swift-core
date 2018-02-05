package com.fr.swift.util.function;

/**
 * @author anchore
 * @date 2017/12/31
 */
public interface Function<Param, Return> {
    Return apply(Param t);
}