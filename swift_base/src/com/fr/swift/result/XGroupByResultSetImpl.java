package com.fr.swift.result;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.sort.Sort;
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
    private List<Map<Integer, Object>> globalDictionaries;
    private List<Map<Integer, Object>> colGlobalDictionaries;
    private List<Sort> indexSorts;
    private List<Sort> colIndexSorts;

    public XGroupByResultSetImpl(List<KeyValue<RowIndexKey<int[]>, List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>>>> resultList,
                                 List<Map<Integer, Object>> globalDictionaries, List<Map<Integer, Object>> colGlobalDictionaries,
                                 List<Sort> indexSorts, List<Sort> colIndexSorts) {
        this.resultList = resultList;
        this.globalDictionaries = globalDictionaries;
        this.colGlobalDictionaries = colGlobalDictionaries;
        this.indexSorts = indexSorts;
        this.colIndexSorts = colIndexSorts;
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
    public List<Sort> getColIndexSorts() {
        return colIndexSorts;
    }

    @Override
    public List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>> getResultList() {
        throw new UnsupportedOperationException();
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
