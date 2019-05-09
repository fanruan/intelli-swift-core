package com.fr.swift.result;

import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.Assert;

import java.io.FilterInputStream;
import java.sql.SQLException;

/**
 * @author anchore
 * @date 2019/3/13
 * <p>
 * 装饰resultset，作用类比{@link FilterInputStream}
 */
public class DecorateResultSet implements SwiftResultSet {

    protected SwiftResultSet origin;

    public DecorateResultSet(SwiftResultSet origin) {
        Assert.notNull(origin);
        this.origin = origin;
    }

    @Override
    public int getFetchSize() {
        return origin.getFetchSize();
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return origin.getMetaData();
    }

    @Override
    public boolean hasNext() throws SQLException {
        return origin.hasNext();
    }

    @Override
    public Row getNextRow() throws SQLException {
        return origin.getNextRow();
    }

    @Override
    public void close() throws SQLException {
        origin.close();
    }
}