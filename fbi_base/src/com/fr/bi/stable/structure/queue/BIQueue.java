package com.fr.bi.stable.structure.queue;

import java.util.Iterator;

/**
 * Created by GUY on 2015/3/16.
 */
public interface BIQueue<T> {

    Iterator<T> iterator();

    boolean contains(T obj);

    void remove(T obj);

    boolean add(T obj);

    boolean isEmpty();

    int size();

    T peek();

    T poll();

    void clear();
}