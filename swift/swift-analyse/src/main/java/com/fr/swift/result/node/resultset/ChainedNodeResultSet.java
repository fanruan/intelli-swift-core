package com.fr.swift.result.node.resultset;

import com.fr.swift.result.BaseNodeResultSet;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.SwiftNode2RowIterator;
import com.fr.swift.result.SwiftNodeOperator;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.structure.Pair;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Lyon
 * @date 2018/6/12
 */
public class ChainedNodeResultSet extends BaseNodeResultSet<SwiftNode> implements NodeResultSet<SwiftNode> {

    private SwiftNodeOperator operator;
    private QueryResultSet<Pair<SwiftNode, List<Map<Integer, Object>>>> source;

    public ChainedNodeResultSet(SwiftNodeOperator operator, QueryResultSet<Pair<SwiftNode, List<Map<Integer, Object>>>> source) {
        super(source.getFetchSize());
        this.operator = operator;
        this.source = source;
    }

    @Override
    public Pair<SwiftNode, List<Map<Integer, Object>>> getPage() {
        Pair<SwiftNode, List<Map<Integer, Object>>> ret = null;
        if (hasNextPage()) {
            Pair<SwiftNode, List<Map<Integer, Object>>> pair = source.getPage();
            ret = operator.apply(pair);
        }
        return ret;
    }

    @Override
    public boolean hasNextPage() {
        return source.hasNextPage();
    }

    @Override
    public SwiftMetaData getMetaData() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasNext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Row getNextRow() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SwiftResultSet convert(final SwiftMetaData metaData) {
        final Iterator<Row> iterator = new SwiftNode2RowIterator(this);
        return new SwiftResultSet() {
            @Override
            public int getFetchSize() {
                return source.getFetchSize();
            }

            @Override
            public SwiftMetaData getMetaData() throws SQLException {
                return metaData;
            }

            @Override
            public boolean hasNext() throws SQLException {
                return iterator.hasNext();
            }

            @Override
            public Row getNextRow() throws SQLException {
                return iterator.next();
            }

            @Override
            public void close() throws SQLException {

            }
        };
    }
}
