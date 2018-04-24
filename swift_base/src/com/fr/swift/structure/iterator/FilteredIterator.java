package com.fr.swift.structure.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Lyon
 * @date 2018/1/5
 */
public class FilteredIterator<T> implements Iterator<T> {
    private Iterator<? extends T> iterator;
    private Filter<T> filter;
    private T nextElement;
    private boolean hasNext;

    /**
     * 包装一个迭代器来实现过滤
     *
     * @param iterator
     * @param filter
     */
    public FilteredIterator(Iterator<? extends T> iterator, Filter<T> filter) {
        this.iterator = iterator;
        this.filter = filter;

        nextMatch();
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public T next() {
        if (!hasNext) {
            throw new NoSuchElementException();
        }

        return nextMatch();
    }

    private T nextMatch() {
        T oldMatch = nextElement;

        while (iterator.hasNext()) {
            T o = iterator.next();

            if (filter.accept(o)) {
                hasNext = true;
                nextElement = o;

                return oldMatch;
            }
        }

        hasNext = false;

        return oldMatch;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
