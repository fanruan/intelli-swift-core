package com.fr.swift.structure.array;

import java.util.Arrays;

/**
 *
 * @author yee
 * @date 2017/4/2
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
