package com.fr.swift.result.node.resultset;

import com.fr.swift.result.BaseNodeResultSet;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.SwiftRowOperator;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.structure.Pair;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Lyon
 * @date 2018/6/13
 */
public class FakeNodeResultSet extends BaseNodeResultSet<SwiftNode> implements NodeResultSet<SwiftNode> {

    private SwiftRowOperator<Row> operator;

    private NodeResultSet<SwiftNode> source;

    private Iterator<Row> rowIterator;

    public FakeNodeResultSet(SwiftRowOperator<Row> operator, NodeResultSet<SwiftNode> source) {
        super(source.getFetchSize());
        this.operator = operator;
        this.source = source;
    }

    @Override
    public Pair<SwiftNode, List<Map<Integer, Object>>> getPage() {
        throw new UnsupportedOperationException();
    }

    private List<Row> getPageData() throws SQLException {
        if (source.hasNextPage()) {
            return operator.operate(source.getPage().getKey());
        }
        return null;
    }

    @Override
    public boolean hasNextPage() {
        return source.hasNextPage();
    }

    @Override
    public SwiftMetaData getMetaData() {
        return null;
    }

    @Override
    public boolean hasNext() throws SQLException {
        if (rowIterator == null && hasNextPage()) {
            rowIterator = getPageData().iterator();
        }
        return rowIterator != null && rowIterator.hasNext();
    }

    @Override
    public Row getNextRow() {
        return rowIterator.next();
    }
}
