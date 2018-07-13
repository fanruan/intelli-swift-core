package com.fr.swift.task.cube.progress;

import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;

/**
 * @author anchore
 * @date 2018/7/12
 */
public class ProgressResultSet extends BaseProgressIterator implements SwiftResultSet {
    private SwiftResultSet resultSet;

    public ProgressResultSet(String source, SwiftResultSet resultSet) {
        super(source);
        this.resultSet = resultSet;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return resultSet.getMetaData();
    }

    @Override
    public boolean hasNext() throws SQLException {
        return resultSet.hasNext();
    }

    @Override
    public Row getNextRow() throws SQLException {
        iterateNext();
        return resultSet.getNextRow();
    }

    @Override
    public void close() throws SQLException {
        iterateOver();
        resultSet.close();
    }
}