package com.fr.swift.result.row;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.result.KeyValue;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/4/2.
 */
public class XGroupByResultSetImpl implements XGroupByResultSet<int[]> {

    private List<KeyValue<RowIndexKey<int[]>, List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>>>> resultList;
    private List<Map<Integer, Object>> rowGlobalDictionaries;
    private List<Map<Integer, Object>> colGlobalDictionaries;
    private int rowDimensionSize;
    private int colDimensionSize;

    public XGroupByResultSetImpl(List<KeyValue<RowIndexKey<int[]>, List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>>>> resultList,
                                 List<Map<Integer, Object>> rowGlobalDictionaries, List<Map<Integer, Object>> colGlobalDictionaries,
                                 int rowDimensionSize, int colDimensionSize) {
        this.resultList = resultList;
        this.rowGlobalDictionaries = rowGlobalDictionaries;
        this.colGlobalDictionaries = colGlobalDictionaries;
        this.rowDimensionSize = rowDimensionSize;
        this.colDimensionSize = colDimensionSize;
    }

    @Override
    public List<KeyValue<RowIndexKey<int[]>, List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>>>> getXResultList() {
        return resultList;
    }

    @Override
    public List<Map<Integer, Object>> getColGlobalDictionaries() {
        return colGlobalDictionaries;
    }

    @Override
    public List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>> getResultList() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Map<Integer, Object>> getRowGlobalDictionaries() {
        return rowGlobalDictionaries;
    }

    @Override
    public int colDimensionSize() {
        return colDimensionSize;
    }

    @Override
    public int rowDimensionSize() {
        return rowDimensionSize;
    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public boolean hasNext() throws SQLException {
        return false;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return null;
    }

    @Override
    public Row getNextRow() throws SQLException {
        return null;
    }
}
