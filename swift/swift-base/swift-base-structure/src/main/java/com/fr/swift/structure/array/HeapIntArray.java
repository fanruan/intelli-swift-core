package com.fr.swift.structure.array;

import java.util.Arrays;

/**
 * Created by 小灰灰 on 2017/5/22.
 */
public class HeapIntArray extends BaseIntArray {
    private int[] items;

    public HeapIntArray(int capacity, int defaultValue) {
        items = new int[capacity];
        Arrays.fill(items, defaultValue);
    }

    public HeapIntArray(int capacity) {
        this.items = new int[capacity];
    }

    @Override
    public void put(int index, int value) {
        items[index] = value;
    }

    @Override
    public int size() {
        return items.length;
    }

    @Override
    public int get(int index) {
        return items[index];
    }

    @Override
    public void release() {
        items = null;
    }
}
