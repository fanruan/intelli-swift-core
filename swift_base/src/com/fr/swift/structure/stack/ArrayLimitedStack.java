package com.fr.swift.structure.stack;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by Lyon on 2018/3/1.
 */
public class ArrayLimitedStack<T> implements LimitedStack<T> {

    private int limit;
    private int size = 0;
    private Object[] items;

    public ArrayLimitedStack(int limit) {
        this.limit = limit;
        items = new Object[limit];
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int limit() {
        return limit;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void push(T item) {
        if (size >= limit) {
            throw new IndexOutOfBoundsException("LimitedStack overflow");
        }
        items[size] = item;
        size += 1;
    }

    @Override
    public T pop() {
        if (isEmpty()) {
            throw new NoSuchElementException("LimitedStack underflow");
        }
        @SuppressWarnings("unchecked")
        T item = (T) items[size - 1];
        items[size - 1] = null;
        size -= 1;
        return item;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("LimitedStack underflow");
        }
        return (T) items[size - 1];
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> toList() {
        List<T> copy = new ArrayList<T>(limit);
        for (int i = 0; i < limit; i++) {
            copy.add((T) items[i]);
        }
        return copy;
    }
}
