package com.fr.swift.structure.queue;

/**
 * Created by Lyon on 2018/3/6.
 */
public interface FIFOQueue<T> {

    void add(T item);

    T remove();

    boolean isEmpty();
}
