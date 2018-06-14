package com.fr.swift.result;

import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 2018/6/13.
 */
public class FakeNodeResult implements NodeResultSet<SwiftNode> {

    private SwiftRowOperator<Row> operator;
    private NodeResultSet source;
    private Iterator<Row> rowIterator;

    public FakeNodeResult(SwiftRowOperator<Row> operator, NodeResultSet source) {
        this.operator = operator;
        this.source = source;
    }

    @Override
    public SwiftNode<SwiftNode> getNode() {
        throw new UnsupportedOperationException();
    }

    public List<Row> getPageDAta() throws SQLException {
        if (source.hasNextPage()) {
            return operator.operate(source.getNode());
        }
        return null;
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
        if (rowIterator == null && hasNextPage()) {
            rowIterator = getPageDAta().iterator();
        }
        return rowIterator != null && rowIterator.hasNext();
    }

    @Override
    public Row getRowData() throws SQLException {
        return rowIterator.next();
    }

    @Override
    public void close() throws SQLException {

    }
}
