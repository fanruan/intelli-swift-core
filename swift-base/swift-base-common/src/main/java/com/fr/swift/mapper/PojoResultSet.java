package com.fr.swift.mapper;

import com.fr.swift.result.SwiftResultSet;

import java.sql.SQLException;

/**
 * @author Hoky
 * @date 2020/9/16
 */
public class PojoResultSet<T> {

    SwiftResultSet swiftResultSet;
    Class<T> tClass;

    public PojoResultSet(SwiftResultSet swiftResultSet, Class<T> tClass) {
        this.swiftResultSet = swiftResultSet;
        this.tClass = tClass;
    }

    public int getFetchSize() {
        return 0;
    }

    public boolean hasNext() throws SQLException {
        return swiftResultSet.hasNext();
    }

    public T getNextPojo() throws Exception {
        return MapperFactory.getMapper().getMappered(swiftResultSet.getNextRow(), swiftResultSet.getMetaData(), tClass);
    }

    public void close() throws SQLException {
        swiftResultSet.close();
    }
}