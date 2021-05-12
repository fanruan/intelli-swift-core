package com.fr.swift.cloud.source.alloter.impl.hash.function;

import java.util.Collections;
import java.util.List;

/**
 * @author Hoky
 * @date 2020/11/11
 */
public abstract class BaseHashFunction implements HashFunction {
    @Override
    public String getCubePath(int logicOrder) {
        return "0";
    }

    @Override
    public List<Integer> divideOf(int index) {
        return Collections.emptyList();
    }
}
