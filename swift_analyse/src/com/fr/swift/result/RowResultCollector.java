package com.fr.swift.result;

import com.fr.swift.mapreduce.InCollector;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;

import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2017/12/29.
 */
public class RowResultCollector implements SwiftResultSet {

    private InCollector<RowIndexKey, AggregatorValue[]> rowResult;
    // 这个要用来合并的
    private List<Map<Integer, Object>> globalDictionaries;
    private List<Sort> indexSorts;

    public RowResultCollector(List<Map<Integer, Object>> globalDictionaries,
                              InCollector<RowIndexKey, AggregatorValue[]> rowResult) {
        this(globalDictionaries, rowResult, null);
    }

    public RowResultCollector(List<Map<Integer, Object>> globalDictionaries,
                              InCollector<RowIndexKey, AggregatorValue[]> rowResult,
                              List<Sort> indexSorts) {
        this.globalDictionaries = globalDictionaries;
        this.rowResult = rowResult;
        this.indexSorts = indexSorts;
    }

    public InCollector<RowIndexKey, AggregatorValue[]> getRowResult() {
        return rowResult;
    }

    public List<Map<Integer, Object>> getGlobalDictionaries() {
        return globalDictionaries;
    }

    public List<Sort> getIndexSorts() {
        return indexSorts;
    }

    public void setRowResult(InCollector<RowIndexKey, AggregatorValue[]> rowResult) {
        this.rowResult = rowResult;
    }

    public void setGlobalDictionaries(List<Map<Integer, Object>> globalDictionaries) {
        this.globalDictionaries = globalDictionaries;
    }

    @Override
    public void close() {

    }

    @Override
    public boolean next() {
        return false;
    }

    @Override
    public SwiftMetaData getMetaData() {
        return null;
    }

    @Override
    public Row getRowData() {
        return null;
    }
}
