package com.fr.swift.source;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/6/11
 */
public class SerializableResultSet implements SwiftResultSet, Serializable {
    private static final long serialVersionUID = -2306723089258907631L;

    private transient SwiftResultSet resultSet;
    private SwiftMetaData metaData;
    private List<Row> rows;
    private int currentRow = -1;

    public SerializableResultSet(SwiftResultSet resultSet) throws SQLException {
        this.resultSet = resultSet;
        this.metaData = resultSet.getMetaData();
        init();
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return metaData;
    }

    public void init() throws SQLException {
        this.rows = new ArrayList<Row>();
        while (resultSet.next()) {
            rows.add(resultSet.getRowData());
        }
        resultSet.close();
    }

    @Override
    public boolean next() throws SQLException {
        return ++currentRow < rows.size();
    }

    @Override
    public Row getRowData() throws SQLException {
        if (currentRow >= rows.size()) {
            throw new SQLException();
        }
        return rows.get(currentRow);
    }

    @Override
    public void close() throws SQLException {
        currentRow = -1;
        rows.clear();
    }
}
