package com.fr.swift.structure.array;

import java.util.Arrays;
import java.util.List;

/**
 * @author yee
 * @date 2017/4/2
 */
public class HeapLongArray implements LongArray {
    private long[] items;

    public HeapLongArray(int capacity, long defaultValue) {
        items = new long[capacity];
        Arrays.fill(items, defaultValue);
    }

    public HeapLongArray(int capacity) {
        items = new long[capacity];
        if (capacity > 0) {
            Arrays.fill(items, Long.MIN_VALUE);
        }
    }

    public HeapLongArray(List<Long> list) {
        items = new long[list.size()];
        for (int i = 0; i < list.size(); i++) {
            items[i] = list.get(i);
        }
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

    @Override
    public LongArray clone() {
        int size = items.length;
        LongArray array = new HeapLongArray(size);
        for (int i = 0; i < size; i++) {
            array.put(i, items[i]);
        }
        return array;
    }
}
