package com.fr.swift.query.adapter;

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
}
