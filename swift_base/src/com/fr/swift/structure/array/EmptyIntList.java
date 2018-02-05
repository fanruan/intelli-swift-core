package com.fr.swift.structure.array;

/**
 * Created by Lyon on 2017/11/27.
 */
public class EmptyIntList implements IntList {

    protected EmptyIntList() {}

    @Override
    public void add(int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int get(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(int index, int val) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
