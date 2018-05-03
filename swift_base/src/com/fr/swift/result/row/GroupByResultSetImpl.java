package com.fr.swift.result.row;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.result.KeyValue;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class GroupByResultSetImpl implements GroupByResultSet<int[]> {

    private List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>> resultList;
    private List<Map<Integer, Object>> globalDictionaries;
    private int rowDimensionSize;

    public GroupByResultSetImpl(List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>> resultList,
                                List<Map<Integer, Object>> globalDictionaries,
                                int rowDimensionSize) {
        this.resultList = resultList;
        this.globalDictionaries = globalDictionaries;
        this.rowDimensionSize = rowDimensionSize;
    }

    @Override
    public List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>> getResultList() {
        return resultList;
    }

    @Override
    public List<Map<Integer, Object>> getRowGlobalDictionaries() {
        return globalDictionaries;
    }

    @Override
    public int rowDimensionSize() {
        return rowDimensionSize;
    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public boolean next() throws SQLException {
        return false;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return null;
    }

    @Override
    public Row getRowData() throws SQLException {
        return null;
    }
}
