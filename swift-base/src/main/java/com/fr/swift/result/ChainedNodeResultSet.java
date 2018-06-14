package com.fr.swift.result;

import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;
import java.util.Iterator;

/**
 * Created by Lyon on 2018/6/12.
 */
public class ChainedNodeResultSet implements NodeResultSet<SwiftNode> {

    private SwiftNodeOperator<SwiftNode> operator;
    private NodeResultSet source;
    private Iterator<Row> rowIterator;

    public ChainedNodeResultSet(SwiftNodeOperator<SwiftNode> operator, NodeResultSet source) {
        this.operator = operator;
        this.source = source;
    }

    @Override
    public SwiftNode<SwiftNode> getNode() {
        SwiftNode ret = null;
        if (hasNextPage()) {
            // TODO: 2018/6/13 当前resultSet要不要缓存上一页的结果呢？
            ret = operator.operate(source.getNode());
        }
        return ret;
    }

    @Override
    public boolean hasNextPage() {
        return source.hasNextPage();
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return null;
    }

    @Override
    public boolean next() throws SQLException {
        if (rowIterator == null) {
            rowIterator = new SwiftRowIteratorImpl(this);
        }
        return rowIterator.hasNext();
    }

    @Override
    public Row getRowData() throws SQLException {
        return rowIterator.next();
    }

    @Override
    public void close() throws SQLException {

    }
}
