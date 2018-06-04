package com.fr.swift.result;

import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.structure.iterator.Tree2RowIterator;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by pony on 2018/4/19.
 * 根据最后一个节点来遍历node，直到没有sibling
 */
public class NodeResultSetImpl<T extends SwiftNode> implements NodeResultSet {

    private SwiftNode<T> node;
    private SwiftMetaData metaData;
    private Iterator<List<SwiftNode>> iterator;

    public NodeResultSetImpl(int dimensionSize, SwiftNode<T> node) {
        this.node = node;
        this.iterator = new Tree2RowIterator(dimensionSize, node.getChildren().iterator());
    }

    public NodeResultSetImpl(int dimensionSize, SwiftNode<T> node, SwiftMetaData metaData) {
        this.node = node;
        this.metaData = metaData;
        this.iterator = new Tree2RowIterator(dimensionSize, node.getChildren().iterator());
    }

    @Override
    public SwiftNode<T> getNode() {
        return node;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return metaData;
    }

    @Override
    public boolean next() throws SQLException {
        return iterator.hasNext();
    }

    @Override
    public Row getRowData() throws SQLException {
        return NodeMergeResultSetImpl.nodes2Row(iterator.next());
    }

    @Override
    public void close() throws SQLException {

    }
}
