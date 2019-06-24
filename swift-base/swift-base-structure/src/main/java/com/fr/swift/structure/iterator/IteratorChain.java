package com.fr.swift.structure.iterator;

import java.util.Iterator;

/**
 * @author yee
 * @date 2019-06-25
 */
public class IteratorChain<T> implements Iterator<T> {

    private Iterator<? extends Iterator<T>> iterator;
    private Iterator<T> current;

    public IteratorChain(Iterator<? extends Iterator<T>> iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
        if (null == this.current) {
            return iterator.hasNext() && (this.current = iterator.next()).hasNext();
        }
        return this.current.hasNext();
    }

    @Override
    public T next() {
        return this.current.next();
    }

    @Override
    public void remove() {

    }
}
