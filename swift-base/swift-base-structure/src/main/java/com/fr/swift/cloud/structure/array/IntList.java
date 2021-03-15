package com.fr.swift.cloud.structure.array;

import com.fr.swift.cloud.util.Clearable;

/**
 * @author 小灰灰
 * @date 2017/5/22
 */
public interface IntList extends Clearable {
    void add(int value);

    int get(int index);

    void set(int index, int val);

    int size();
}