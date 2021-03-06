package com.fr.swift.cloud.structure.array;

import com.fr.swift.cloud.structure.IntIterable;

/**
 * @author 小灰灰
 * @date 2017/5/22
 */
public interface IntArray extends IntIterable {
    void put(int index, int value);

    int size();

    int get(int index);

    void release();
}
