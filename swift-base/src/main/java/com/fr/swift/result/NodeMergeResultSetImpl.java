package com.fr.swift.result;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.structure.iterator.Tree2RowIterator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * TODO 简单用Tree2RowIterator实现了一下，具体怎么实现还不知道
 * Created by Lyon on 2018/4/27.
 */
public class NodeMergeResultSetImpl<T extends GroupNode> implements NodeMergeResultSet<T> {

    private GroupNode<T> root;
    private List<Map<Integer, Object>> rowGlobalDictionaries;
    private List<Aggregator> aggregators;
    private Tree2RowIterator tree2RowIterator;

    public NodeMergeResultSetImpl(GroupNode<T> root, List<Map<Integer, Object>> rowGlobalDictionaries,
                                  List<Aggregator> aggregators) {
        this.root = root;
        this.rowGlobalDictionaries = rowGlobalDictionaries;
        this.aggregators = aggregators;
        this.tree2RowIterator = new Tree2RowIterator(rowGlobalDictionaries.size(), root.getChildren().iterator());
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
        return tree2RowIterator.hasNext();
    }

    @Override
    public Row getRowData() throws SQLException {
        List<T> row = tree2RowIterator.next();
        List data = new ArrayList();
        if (null != row) {
            for (T col : row) {
                if (null != col) {
                    data.add(col.getData());
                } else {
                    data.add(null);
                }
            }
        }
        return new ListBasedRow(data);
    }

    @Override
    public void close() throws SQLException {

    }
}
