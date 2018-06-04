package com.fr.swift.result;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.structure.iterator.Tree2RowIterator;
import com.fr.swift.util.function.Function;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/4/27.
 */
public class NodeMergeResultSetImpl<T extends GroupNode> implements NodeMergeResultSet<T> {

    private GroupNode root;
    private List<Map<Integer, Object>> rowGlobalDictionaries;
    private List<Aggregator> aggregators;
    private Iterator<List<SwiftNode>> tree2RowIterator;

    public NodeMergeResultSetImpl(GroupNode root, List<Map<Integer, Object>> rowGlobalDictionaries,
                                  List<Aggregator> aggregators) {
        this.root = root;
        this.rowGlobalDictionaries = rowGlobalDictionaries;
        this.aggregators = aggregators;
        this.tree2RowIterator = new Tree2RowIterator<SwiftNode>(rowGlobalDictionaries.size(), root.getChildren().iterator(), new Function<SwiftNode, Iterator<SwiftNode>>() {
            @Override
            public Iterator<SwiftNode> apply(SwiftNode p) {
                return p.getChildren().iterator();
            }
        });
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
        List<SwiftNode> row = tree2RowIterator.next();
        return nodes2Row(row);
    }

    static Row nodes2Row(List<SwiftNode> row) {
        List data = new ArrayList();
        if (null != row) {
            for (SwiftNode col : row) {
                if (null != col) {
                    data.add(col.getData());
                } else {
                    data.add(null);
                }
            }
        }
        if (null != row) {
            SwiftNode leafNode = row.get(row.size() - 1);
            AggregatorValue[] values = leafNode.getAggregatorValue();
            values = values == null ? new AggregatorValue[0] : values;
            for (int i = 0; i < values.length; i++) {
                data.add(values[i] == null ? null : values[i].calculateValue());
            }
        }
        return new ListBasedRow(data);
    }

    @Override
    public void close() throws SQLException {

    }
}
