package com.fr.swift.mapper;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;

/**
 * @author lucifer
 * @date 2020/3/13
 * @description
 * @since swift 1.1
 */
public class MapperedResultSet<T> implements SwiftResultSet {

    private static MapperFactory mapperFactory = MapperFactory.getMapper();

    private SwiftResultSet swiftResultSet;

    private Class<T> clazz;

    public MapperedResultSet(SwiftResultSet swiftResultSet, Class<T> clazz) {
        this.swiftResultSet = swiftResultSet;
        this.clazz = clazz;
    }

    @Override
    public int getFetchSize() {
        return swiftResultSet.getFetchSize();
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return swiftResultSet.getMetaData();
    }

    @Override
    public boolean hasNext() throws SQLException {
        return swiftResultSet.hasNext();
    }

    @Override
    public Row getNextRow() throws SQLException {
        return swiftResultSet.getNextRow();
    }

    public T getNextBean() throws Exception {
        return mapperFactory.getMappered(getNextRow(), swiftResultSet.getMetaData(), clazz);
    }

    @Override
    public void close() throws SQLException {
        swiftResultSet.close();
    }
}
