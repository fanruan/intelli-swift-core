package com.fr.swift.source;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by lifan on 2019-06-14 15:00
 */
public class ListRelationRow implements RelationRow {

    private List<Object> values;

    public ListRelationRow(Row row) {
        values = new ArrayList<Object>();
        for (int i=0;i<row.getSize();i++) {
            values.add(row.getValue(i));
        }
    }

    public ListRelationRow(List<Object> values) {
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
    public void addAllRowElement(RelationRow relationRow) {
        for (int i=0;i<relationRow.getSize();i++) {
            values.add(relationRow.getValue(i));
        }
    }

    @Override
    public List<Object> getValues() {
        return values;
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
