package com.fr.swift.result;

import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/8/29
 */
public enum EmptyResultSet implements SwiftResultSet {

    INSTANCE;

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return new SwiftMetaDataBean();
    }

    @Override
    public boolean hasNext() throws SQLException {
        return false;
    }

    @Override
    public Row getNextRow() throws SQLException {
        return null;
    }

    @Override
    public void close() throws SQLException {

    }
}
