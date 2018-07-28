package com.fr.swift.result;

/**
 * Created by Lyon on 2018/7/25.
 */
public abstract class AbstractDetailResultSet implements DetailResultSet {

    protected int fetchSize;

    public AbstractDetailResultSet(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    @Override
    public int getFetchSize() {
        return fetchSize;
    }
}
