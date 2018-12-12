package com.fr.swift.result;

import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.structure.Pair;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by pony on 2018/4/19.
 */
public class NodeResultSetImpl<T extends SwiftNode> extends BaseNodeResultSet implements NodeResultSet {

    private int fetchSize;
    private SwiftNode node;
    private SwiftMetaData metaData;
    private Iterator<Row> iterator;

    public NodeResultSetImpl(int fetchSize, SwiftNode node, SwiftMetaData metaData) {
        this.fetchSize = fetchSize;
        this.node = node;
        this.metaData = metaData;
    }

    @Override
    public int getFetchSize() {
        return fetchSize;
    }

    @Override
    public Pair<SwiftNode, List<Map<Integer, Object>>> getPage() {
        return Pair.of(node, null);
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
    public boolean hasNext() throws SQLException {
        if (iterator == null) {
            iterator = SwiftNodeUtils.node2RowIterator(node);
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
