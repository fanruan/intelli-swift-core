package com.fr.swift.structure.array;

/**
 * @author yee
 * @date 2018/4/2
 */
public interface LongArray {
    void put(int index, long value);

    int size();

    long get(int index);

    void release();

    LongArray clone();
}
