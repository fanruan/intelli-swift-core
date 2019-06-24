package com.fr.swift.source;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Create by lifan on 2019-06-14 15:00
 */
public class ListMutableRow implements MutableRow {

    private List<Object> values;

    public ListMutableRow(Row row) {
        values = new ArrayList<Object>();
        for (int i = 0; i < row.getSize(); i++) {
            values.add(row.getValue(i));
        }
    }

    public ListMutableRow(Object... values) {
        this.values = Arrays.asList(values);
    }

    public ListMutableRow(List<Object> values) {
        this.values = values;
    }

    @Override
    public Object getValue(int index) {
        return values.get(index);
    }

    @Override
    public int getSize() {
        return values.size();
    }

    @Override
    public void addElement(Object object) {
        values.add(object);
    }

    @Override
    public void addAllRowElement(MutableRow mutableRow) {
        for (int i = 0; i < mutableRow.getSize(); i++) {
            values.add(mutableRow.getValue(i));
        }
    }

    @Override
    public void setRow(List<Object> values) {
        this.values = values;
    }

    @Override
    public void removeElement(int index) {
        values.remove(index);
    }

    @Override
    public String toString() {
        return values.toString();
    }
}
