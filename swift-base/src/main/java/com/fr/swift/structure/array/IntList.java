package com.fr.swift.structure.array;

import com.fr.swift.util.Clearable;

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