package com.fr.swift.structure.array;

import java.util.Arrays;

/**
 * Created by 小灰灰 on 2017/5/22.
 */
public class HeapLongArray implements LongArray {
    private long[] items;

    public HeapLongArray(int capacity, long defaultValue) {
        items = new long[capacity];
        Arrays.fill(items, defaultValue);
    }

    @Override
    public void put(int index, long value) {
        items[index] = value;
    }

    @Override
    public int size() {
        return items.length;
    }

    @Override
    public long get(int index) {
        return items[index];
    }

    @Override
    public void release() {
        items = null;
    }
}
