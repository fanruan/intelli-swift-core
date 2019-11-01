package com.fr.swift.structure;

import java.util.Iterator;

/**
 * @author anchore
 * @date 2018/12/12
 */
public interface IntIterable extends Iterable<Integer> {

    IntIterator intIterator();

    abstract class IntIterator implements Iterator<Integer> {
        public abstract int nextInt();

        @Override
        public Integer next() {
            return nextInt();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}