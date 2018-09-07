package com.fr.swift.structure.array;

import java.util.Arrays;

/**
 * @author 小灰灰
 * @date 2017/5/22
 */
public class HeapIntList implements IntList {
    private int[] items;
    private int size;

    protected HeapIntList() {
        this(16);
    }

    protected HeapIntList(int capacity) {
        items = new int[capacity];
    }

    protected HeapIntList(int capacity, int defaultValue) {
        items = new int[capacity];
        size = capacity;
        Arrays.fill(items, defaultValue);
    }

    @Override
    public void add(int value) {
        int[] items = this.items;
        if (size == items.length) {
            items = resize(Math.max(8, (int) (size * 1.75f)));
        }
        items[size++] = value;
    }

    @Override
    public int get(int index) {
        if (index >= size) {
            throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + size);
        }
        return items[index];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void set(int index, int val) {
        if (index >= size) {
            throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + size);
        }
        items[index] = val;
    }

    @Override
    public void clear() {
        size = 0;
        items = null;
    }

    private int[] resize(int newSize) {
        int[] newItems = new int[newSize];
        int[] items = this.items;
        System.arraycopy(items, 0, newItems, 0, Math.min(size, newItems.length));
        this.items = newItems;
        return newItems;
    }

    public int[] toArray() {
        int[] array = new int[size()];
        System.arraycopy(items, 0, array, 0, size());
        return array;
    }
}
