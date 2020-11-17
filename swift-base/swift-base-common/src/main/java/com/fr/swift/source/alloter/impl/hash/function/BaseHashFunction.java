package com.fr.swift.source.alloter.impl.hash.function;

/**
 * @author Hoky
 * @date 2020/11/11
 */
public abstract class BaseHashFunction implements HashFunction {
    @Override
    public String getCubePath(int logicOrder) {
        return "0";
    }
}
