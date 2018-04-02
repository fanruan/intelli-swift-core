package com.fr.swift.structure.array;

/**
 *
 * @author 小灰灰
 * @date 2017/5/22
 */
public interface LongArray {
    void put(int index, long value);

    int size();

    long get(int index);

    void release();
}
