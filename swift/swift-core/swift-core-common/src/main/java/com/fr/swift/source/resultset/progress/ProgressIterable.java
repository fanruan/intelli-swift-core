package com.fr.swift.source.resultset.progress;

import java.util.Iterator;

/**
 * @author anchore
 * @date 2018/7/12
 */
public class ProgressIterable<T> implements Iterable<T> {
    private Iterable<T> iterable;

    public ProgressIterable(Iterable<T> iterable) {
        this.iterable = iterable;
    }

    public static <E> Iterable<E> of(Iterable<E> iterable) {
        return new ProgressIterable<E>(iterable);
    }

    @Override
    public Iterator<T> iterator() {
        return new ProgressIterator<T>("", iterable.iterator());
    }
}