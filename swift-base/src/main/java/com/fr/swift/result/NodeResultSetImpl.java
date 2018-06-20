package com.fr.swift.result;

import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;
import java.util.Iterator;

/**
 * Created by pony on 2018/4/19.
 */
public class NodeResultSetImpl<T extends SwiftNode> implements NodeResultSet {

    private SwiftNode node;
    private SwiftMetaData metaData;
    private Iterator<Row> iterator;

    public NodeResultSetImpl(SwiftNode node) {
        this.node = node;
    }

    public NodeResultSetImpl(SwiftNode node, SwiftMetaData metaData) {
        this.node = node;
        this.metaData = metaData;
    }

    @Override
    public SwiftNode<T> getNode() {
        return node;
    }

    @Override
    public boolean hasNextPage() {
        return false;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return metaData;
    }

    @Override
    public boolean next() throws SQLException {
        if (iterator == null) {
            iterator = SwiftNodeUtils.node2RowIterator(node);
        }
        return iterator.hasNext();
    }

    @Override
    public Row getRowData() throws SQLException {
        return iterator.next();
    }

    @Override
    public void close() throws SQLException {

    }
}
