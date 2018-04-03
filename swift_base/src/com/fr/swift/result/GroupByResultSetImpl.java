package com.fr.swift.result;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GroupByResultSetImpl implements GroupByResultSet<int[]> {

    private List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>> resultList;
    private List<Map<Integer, Object>> globalDictionaries;
    private List<Sort> indexSorts;

    public GroupByResultSetImpl(List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>> resultList,
                                List<Map<Integer, Object>> globalDictionaries, List<Sort> indexSorts) {
        this.resultList = resultList;
        this.globalDictionaries = globalDictionaries;
        this.indexSorts = indexSorts;
    }

    @Override
    public List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>> getResultList() {
        return resultList;
    }

    @Override
    public List<Map<Integer, Object>> getGlobalDictionaries() {
        return globalDictionaries;
    }

    @Override
    public List<Sort> getIndexSorts() {
        return indexSorts;
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
