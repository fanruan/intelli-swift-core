package com.fr.swift.result;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GroupByResultSetImpl implements GroupByResultSet {

    private Iterator<KeyValue<int[], AggregatorValue[]>> rowResultIterator;
    private List<Map<Integer, Object>> globalDictionaries;
    private List<Sort> indexSorts;

    public GroupByResultSetImpl(Iterator<KeyValue<int[], AggregatorValue[]>> rowResultIterator,
                                List<Map<Integer, Object>> globalDictionaries, List<Sort> indexSorts) {
        this.rowResultIterator = rowResultIterator;
        this.globalDictionaries = globalDictionaries;
        this.indexSorts = indexSorts;
    }

    @Override
    public Iterator<KeyValue<int[], AggregatorValue[]>> getRowResultIterator() {
        return rowResultIterator;
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
