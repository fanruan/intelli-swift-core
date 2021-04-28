package com.fr.swift.cloud.analyse.merged;

import com.fr.swift.cloud.source.Row;
import com.fr.swift.cloud.source.SwiftMetaData;

import java.io.Serializable;
import java.sql.SQLException;

/**
 * @author xiqiu
 * @date 2021/1/18
 * @description
 * @since swift-1.2.0
 */

public class EmptySwiftResultSet extends BaseDetailResultSet implements Serializable {
    private static final long serialVersionUID = 4423119475976180073L;


    /**
     * @return EmptySwiftResultSet
     * @deprecated using {@link #create()} instead
     */
    @Deprecated
    public static EmptySwiftResultSet get() {
        return create();
    }

    public static EmptySwiftResultSet create() {
        return new EmptySwiftResultSet();
    }

    public EmptySwiftResultSet() {
        this.swiftMetaData = null;
    }

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public void setMetaData(SwiftMetaData swiftMetaData) {
        this.swiftMetaData = swiftMetaData;
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
        // ignore
    }

}