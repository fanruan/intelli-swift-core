package com.fr.swift.util.function;

/**
 * Created by Lyon on 2018/4/27.
 */
public interface BinaryFunction<Param1, Param2, Return> {
    Return apply(Param1 param1, Param2 param2);
}