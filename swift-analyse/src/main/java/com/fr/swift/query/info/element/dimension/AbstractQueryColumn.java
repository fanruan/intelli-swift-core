package com.fr.swift.query.info.element.dimension;

/**
 * Created by pony on 2017/12/22.
 */
public class AbstractQueryColumn implements QueryColumn {
    private int index;

    public AbstractQueryColumn(int index) {
        this.index = index;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractQueryColumn that = (AbstractQueryColumn) o;

        return index == that.index;
    }

    @Override
    public int hashCode() {
        return index;
    }
}
