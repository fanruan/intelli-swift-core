package com.fr.swift.source;

import java.util.List;

public class ListBasedRow implements Row {
    private List<Object> values;

    public ListBasedRow(List<Object> values) {
        this.values = values;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> V getValue(int index) {
        return (V) values.get(index);
    }

    @Override
    public int getSize() {
        return values == null ? 0 : values.size();
    }

    @Override
    public String toString() {
        return values.toString();
    }
}
