package com.fr.swift.analyse.merged;

import com.fr.swift.config.entity.SwiftMetaDataEntity;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

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

    private static final EmptySwiftResultSet INSTANCE = new EmptySwiftResultSet();


    public static EmptySwiftResultSet get() {
        return INSTANCE;
    }

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public void setMetaData(SwiftMetaData swiftMetaData) {
        this.swiftMetaData = new SwiftMetaDataEntity();
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return swiftMetaData;
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