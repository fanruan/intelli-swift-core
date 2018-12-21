package com.fr.swift.source;

import com.fr.third.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class ListBasedRow implements Row {
    private static final long serialVersionUID = -5283575301909309763L;

    private List<?> values;

    public ListBasedRow() {
    }

    public ListBasedRow(List<?> values) {
        this.values = values;
    }

    @Override
    @SuppressWarnings("unchecked")
    @JsonIgnore
    public <V> V getValue(int index) {
        return (V) values.get(index);
    }

    @Override
    @JsonIgnore
    public int getSize() {
        return values == null ? 0 : values.size();
    }

    @Override
    public String toString() {
        return values.toString();
    }

    public List<Object> getValues() {
        return new ArrayList<Object>(values);
    }

    public void setValues(List<Object> values) {
        this.values = values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ListBasedRow row = (ListBasedRow) o;

        return values != null ? values.equals(row.values) : row.values == null;
    }

    @Override
    public int hashCode() {
        return values != null ? values.hashCode() : 0;
    }
}