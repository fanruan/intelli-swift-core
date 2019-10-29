package com.fr.swift.result.node.resultset;

import com.fr.swift.result.BaseNodeQueryResultSet;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.SwiftNode2RowIterator;
import com.fr.swift.result.SwiftNodeOperator;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.util.Iterator;

/**
 * @author Lyon
 * @date 2018/6/12
 */
public class ChainedNodeQueryResultSet extends BaseNodeQueryResultSet {

    private SwiftNodeOperator operator;
    private QueryResultSet<SwiftNode> source;

    public ChainedNodeQueryResultSet(SwiftNodeOperator operator, QueryResultSet<SwiftNode> source) {
        super(source.getFetchSize());
        this.operator = operator;
        this.source = source;
    }

    @Override
    public SwiftNode getPage() {
        SwiftNode ret = null;
        if (hasNextPage()) {
            ret = operator.apply(source.getPage());
        }
        return ret;
    }

    @Override
    public boolean hasNextPage() {
        return source.hasNextPage();
    }

    @Override
    public SwiftResultSet convert(final SwiftMetaData metaData) {
        final Iterator<Row> iterator = new SwiftNode2RowIterator(this);
        return create(getFetchSize(), iterator, metaData);
    }

    static SwiftResultSet create(final int fetchSize, final Iterator<Row> iterator, final SwiftMetaData metaData) {
        return new SwiftResultSet() {
            @Override
            public int getFetchSize() {
                return fetchSize;
            }

            @Override
            public SwiftMetaData getMetaData() {
                return metaData;
            }

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Row getNextRow() {
                return iterator.next();
            }

            @Override
            public void close() {

            }
        };
    }
}
