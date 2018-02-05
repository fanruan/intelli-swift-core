package com.fr.swift.result;

/**
 * Created by Lyon on 18-1-1.
 */
public class Node extends IndexNode {

    public Node(int sumLength, Object data) {
        super(sumLength);
        this.data = data;
    }

    @Override
    protected void initDataByIndex() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Object createKey() {
        return data;
    }
}
