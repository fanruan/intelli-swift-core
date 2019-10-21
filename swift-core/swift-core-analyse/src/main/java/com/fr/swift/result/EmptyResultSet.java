package com.fr.swift.result;

import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

/**
 * @author yee
 * @date 2018/8/29
 */
public enum EmptyResultSet implements SwiftResultSet {
    //
    INSTANCE;

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public SwiftMetaData getMetaData() {
        return new SwiftMetaDataBean();
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public Row getNextRow() {
        return null;
    }

    @Override
    public void close() {

    }
}
