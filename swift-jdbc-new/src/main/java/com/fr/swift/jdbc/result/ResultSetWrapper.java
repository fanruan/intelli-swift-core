package com.fr.swift.jdbc.result;

import com.fr.swift.jdbc.exception.Exceptions;
import com.fr.swift.result.SwiftResultSet;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

/**
 *
 * @author pony
 * @date 2018/8/17
 */
public class ResultSetWrapper extends BaseResultSet {
    private SwiftResultSet resultSet;
    private Map<String, Integer> label2Index;

    public ResultSetWrapper(SwiftResultSet resultSet, Map<String, Integer> label2Index) {
        this.resultSet = resultSet;
        this.label2Index = label2Index;
    }

    @Override
    public boolean next() throws SQLException {
        if (resultSet.hasNext()) {
            current = resultSet.getNextRow();
            return true;
        }
        return false;
    }

    @Override
    public void close() throws SQLException {
        resultSet.close();
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return new MetadataWrapper(resultSet.getMetaData());
    }

    @Override
    public int findColumn(String columnLabel) throws SQLException {
        if (label2Index == null || !label2Index.containsKey(columnLabel)) {
            throw Exceptions.sql(columnLabel + " not found.");
        }
        return label2Index.get(columnLabel);
    }
}
