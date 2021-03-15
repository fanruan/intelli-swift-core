package com.fr.swift.cloud.result.node.resultset;

import com.fr.swift.cloud.result.BaseNodeQueryResultSet;
import com.fr.swift.cloud.result.SwiftNode;
import com.fr.swift.cloud.result.SwiftNode2RowIterator;
import com.fr.swift.cloud.result.SwiftNodeOperator;
import com.fr.swift.cloud.result.SwiftResultSet;
import com.fr.swift.cloud.result.qrs.QueryResultSet;
import com.fr.swift.cloud.source.Row;
import com.fr.swift.cloud.source.SwiftMetaData;

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
