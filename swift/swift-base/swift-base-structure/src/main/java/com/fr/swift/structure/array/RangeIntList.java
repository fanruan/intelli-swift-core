package com.fr.swift.structure.array;

/**
 * @author Lyon
 * @date 2017/11/27
 */
class RangeIntList implements IntList {
    private int startIndex;
    private int size;

    RangeIntList(int startIndex, int endIndexIncluded) {
        if (endIndexIncluded - startIndex < 0) {
            throw new IllegalArgumentException();
        }
        this.startIndex = startIndex;
        this.size = endIndexIncluded - startIndex + 1;
    }

    @Override
    public void add(int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int get(int index) {
        if (index >= size) {
            throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + size);
        }
        return startIndex + index;
    }

    @Override
    public void set(int index, int val) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}