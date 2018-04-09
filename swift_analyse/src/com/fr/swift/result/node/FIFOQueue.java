package com.fr.swift.result.node;

/**
 * 主要用于树结构的广度优先遍历
 *
 * Created by Lyon on 2018/3/6.
 */
public interface FIFOQueue<T> {

    void add(T item);

    T remove();

    boolean isEmpty();
}
