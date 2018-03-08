package com.fr.swift.structure.stack;

import java.util.NoSuchElementException;

/**
 * Created by Lyon on 2018/3/1.
 */
public class ArrayStack<T> implements Stack<T> {

    private int limit;
    private int size = 0;
    private T[] items;

    public ArrayStack(int limit) {
        this.limit = limit;
        items = (T[]) new Object[limit];
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
            throw new IndexOutOfBoundsException("Stack overflow");
        }
        items[size] = item;
        size += 1;
    }

    @Override
    public T pop() {
        if (isEmpty()) {
            throw new NoSuchElementException("Stack underflow");
        }
        T item = items[size - 1];
        items[size - 1] = null;
        size -= 1;
        return item;
    }

    @Override
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Stack underflow");
        }
        return items[size - 1];
    }
}
