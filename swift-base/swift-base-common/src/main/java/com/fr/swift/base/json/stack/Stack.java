package com.fr.swift.base.json.stack;

import com.fr.swift.base.json.exception.JsonParseException;

import java.util.EmptyStackException;

/**
 * @author yee
 * @date 2018-12-12
 */
public class Stack {

    private final int SIZE;
    private final StackValue[] array;
    protected int pos = 0;

    public Stack() {
        this.SIZE = 100;
        this.array = new StackValue[this.SIZE];
    }

    public boolean isEmpty() {
        return pos == 0;
    }

    public void push(StackValue obj) {
        if (pos == SIZE) {
            throw new StackOverflowError("Maximum depth reached when parse JSON string.");
        }
        array[pos] = obj;
        pos++;
    }

    public StackValue pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        pos--;
        return array[pos];
    }

    public StackValue pop(int type) {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        pos--;
        StackValue obj = array[pos];
        if (obj.getType() == type) {
            return obj;
        }
        throw new JsonParseException("Unmatched object or array.");
    }

    public Class<?> getTopValueClass() {
        StackValue obj = array[pos - 1];
        return obj.getValue().getClass();
    }

    public int getTopValueType() {
        StackValue obj = array[pos - 1];
        return obj.getType();
    }

    public StackValue peek(int type) {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        StackValue obj = array[pos - 1];
        if (obj.getType() == type) {
            return obj;
        }
        throw new JsonParseException("Unmatched object or array.");
    }
}
