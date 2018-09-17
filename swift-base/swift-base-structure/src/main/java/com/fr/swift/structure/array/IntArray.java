package com.fr.swift.structure.array;

/**
 * @author 小灰灰
 * @date 2017/5/22
 */
public interface IntArray {
    void put(int index, int value);

    int size();

    int get(int index);

    void release();
}
