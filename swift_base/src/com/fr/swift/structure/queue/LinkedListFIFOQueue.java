package com.fr.swift.structure.queue;

import java.util.LinkedList;

/**
 * Created by Lyon on 2018/3/6.
 */
public class LinkedListFIFOQueue<T> implements FIFOQueue<T> {

    LinkedList<T> linkedList = new LinkedList<T>();

    @Override
    public void add(T item) {
        linkedList.add(item);
    }

    @Override
    public T remove() {
        return linkedList.removeFirst();
    }

    @Override
    public boolean isEmpty() {
        return linkedList.size() == 0;
    }
}
