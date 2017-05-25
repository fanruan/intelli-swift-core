package com.fr.bi.stable.structure.array;

import java.util.Arrays;

/**
 * Created by 小灰灰 on 2017/5/22.
 */
public class HeapIntList implements IntList{
    public int[] items;
    public int size;
    protected HeapIntList() {
        this(16);
    }

    protected HeapIntList(int capacity) {
        items = new int[capacity];
    }

    protected HeapIntList(int capacity, int defaultValue) {
        items = new int[capacity];
        Arrays.fill(items, defaultValue);
    }


    public void add (int value) {
        int[] items = this.items;
        if (size == items.length) {
            items = resize(Math.max(8, (int)(size * 1.75f)));
        }
        items[size++] = value;
    }


    public int get (int index) {
        if (index >= size) {
            throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + size);
        }
        return items[index];
    }

    @Override
    public int size() {
        return size;
    }

    public void set (int index, int value) {
        if (index >= size) {
            throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + size);
        }
        items[index] = value;
    }


    public void clear () {
        size = 0;
        items = null;
    }

    protected int[] resize (int newSize) {
        int[] newItems = new int[newSize];
        int[] items = this.items;
        System.arraycopy(items, 0, newItems, 0, Math.min(size, newItems.length));
        this.items = newItems;
        return newItems;
    }

}
