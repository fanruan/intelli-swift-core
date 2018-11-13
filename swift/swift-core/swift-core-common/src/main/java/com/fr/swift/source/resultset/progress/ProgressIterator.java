package com.fr.swift.source.resultset.progress;

import java.util.Iterator;

/**
 * @author anchore
 * @date 2018/7/12
 */
public class ProgressIterator<T> extends BaseProgressIterator implements Iterator<T> {
    private Iterator<T> itr;

    public ProgressIterator(String source, Iterator<T> itr) {
        super(source);
        this.itr = itr;
    }

    @Override
    public boolean hasNext() {
        boolean hasNext = itr.hasNext();
        if (!hasNext) {
            iterateOver();
        }
        return hasNext;
    }

    @Override
    public T next() {
        iterateNext();
        return itr.next();
    }

    @Override
    public void remove() {
        itr.remove();
    }
}