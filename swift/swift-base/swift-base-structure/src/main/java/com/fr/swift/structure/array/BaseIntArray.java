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
                return get(cursor++);
            }

            @Override
            public boolean hasNext() {
                return cursor < size();
            }

            @Override
            public Integer next() {
                return nextInt();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public Iterator<Integer> iterator() {
        return intIterator();
    }
}