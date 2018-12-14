package com.fr.swift.result;

import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.structure.Pair;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/4/27.
 */
public class NodeMergeResultSetImpl<T extends GroupNode> extends BaseNodeResultSet<T> implements NodeMergeResultSet<T> {

    private int fetchSize;
    private T root;
    private List<Map<Integer, Object>> rowGlobalDictionaries;
    private Iterator<Row> iterator;
    private boolean hasNextPage = true;

    public NodeMergeResultSetImpl(int fetchSize, T root, List<Map<Integer, Object>> rowGlobalDictionaries) {
        this.fetchSize = fetchSize;
        this.root = root;
        this.rowGlobalDictionaries = rowGlobalDictionaries;
    }

    @Override
    public int getFetchSize() {
        return fetchSize;
    }

    @Override
    public Pair<T, List<Map<Integer, Object>>> getPage() {
        // 只有一页，适配ChainedResultSet
        hasNextPage = false;
        return Pair.of(root, rowGlobalDictionaries);
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
