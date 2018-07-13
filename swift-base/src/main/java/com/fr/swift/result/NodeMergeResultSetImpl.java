package com.fr.swift.result;

import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/4/27.
 */
public class NodeMergeResultSetImpl<T extends GroupNode> implements NodeMergeResultSet<T> {

    private boolean hasNextPage = true;
    private GroupNode root;
    private List<Map<Integer, Object>> rowGlobalDictionaries;
    private Iterator<Row> iterator;

    public NodeMergeResultSetImpl(GroupNode root, List<Map<Integer, Object>> rowGlobalDictionaries) {
        this.root = root;
        this.rowGlobalDictionaries = rowGlobalDictionaries;
    }

    @Override
    public List<Map<Integer, Object>> getRowGlobalDictionaries() {
        return rowGlobalDictionaries;
    }

    @Override
    public SwiftNode<T> getNode() {
        // 只有一页，适配ChainedResultSet
        hasNextPage = false;
        return root;
    }

    @Override
    public boolean hasNextPage() {
        return hasNextPage;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return null;
    }

    @Override
    public boolean hasNext() throws SQLException {
        if (iterator == null) {
            iterator = SwiftNodeUtils.node2RowIterator(root);
        }
        return iterator.hasNext();
    }

    @Override
    public Row getNextRow() throws SQLException {
        return iterator.next();
    }

    @Override
    public void close() throws SQLException {

    }
}
