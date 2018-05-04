package com.fr.swift.result;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/4/27.
 */
public class NodeMergeResultSetImpl<T extends GroupNode> implements NodeMergeResultSet<T> {

    private GroupNode<T> root;
    private List<Map<Integer, Object>> rowGlobalDictionaries;
    private List<Aggregator> aggregators;

    public NodeMergeResultSetImpl(GroupNode<T> root, List<Map<Integer, Object>> rowGlobalDictionaries,
                                  List<Aggregator> aggregators) {
        this.root = root;
        this.rowGlobalDictionaries = rowGlobalDictionaries;
        this.aggregators = aggregators;
    }

    @Override
    public List<Map<Integer, Object>> getRowGlobalDictionaries() {
        return rowGlobalDictionaries;
    }

    @Override
    public List<Aggregator> getAggregators() {
        return aggregators;
    }

    @Override
    public SwiftNode<T> getNode() {
        return root;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return null;
    }

    @Override
    public boolean next() throws SQLException {
        return false;
    }

    @Override
    public Row getRowData() throws SQLException {
        return null;
    }

    @Override
    public void close() throws SQLException {

    }
}
