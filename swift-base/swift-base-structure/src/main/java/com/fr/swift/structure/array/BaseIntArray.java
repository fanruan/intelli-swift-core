package com.fr.swift.structure.array;

import java.util.Iterator;

/**
 * @author anchore
 * @date 2018/12/12
 */
abstract class BaseIntArray implements IntArray {

    @Override
    public IntIterator intIterator() {
        return new IntIterator() {
            private int cursor = 0;

            @Override
            public int nextInt() {
                final int i = get(cursor);
                cursor++;
                return i;
            }

            @Override
            public boolean hasNext() {
                return cursor < size();
            }
        };
    }

    @Override
    public Iterator<Integer> iterator() {
        return intIterator();
    }
}