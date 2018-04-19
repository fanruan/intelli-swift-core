package com.fr.swift.result;

import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;

/**
 * Created by pony on 2018/4/19.
 * 根据最后一个节点来遍历node，直到没有sibling
 */
public class NodeResultSetImpl<T extends SwiftNode> implements NodeResultSet {
    private SwiftNode<T> node;
    private SwiftNode<T> currentChild;

    public NodeResultSetImpl(SwiftNode<T> node) {
        this.node = node;
    }

    @Override
    public SwiftNode<T> getNode() {
        return node;
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
