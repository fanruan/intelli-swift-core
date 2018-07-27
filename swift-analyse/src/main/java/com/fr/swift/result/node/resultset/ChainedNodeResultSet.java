package com.fr.swift.result.node.resultset;

import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.SwiftNode2RowIterator;
import com.fr.swift.result.SwiftNodeOperator;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.util.Iterator;

/**
 * Created by Lyon on 2018/6/12.
 */
public class ChainedNodeResultSet implements NodeResultSet<SwiftNode> {

    private SwiftNodeOperator<SwiftNode> operator;
    private NodeResultSet source;
    private SwiftMetaData metaData;
    private Iterator<Row> rowIterator;

    public ChainedNodeResultSet(SwiftNodeOperator<SwiftNode> operator, NodeResultSet source) {
        this.operator = operator;
        this.source = source;
    }

    public ChainedNodeResultSet(SwiftNodeOperator<SwiftNode> operator, NodeResultSet source, SwiftMetaData metaData) {
        this(operator, source);
        this.metaData = metaData;
    }

    @Override
    public int getFetchSize() {
        return source.getFetchSize();
    }

    @Override
    public SwiftNode<SwiftNode> getNode() {
        SwiftNode ret = null;
        if (hasNextPage()) {
            ret = operator.operate(source.getNode());
        }
        return ret;
    }

    @Override
    public boolean hasNextPage() {
        return source.hasNextPage();
    }

    @Override
    public SwiftMetaData getMetaData() {
        return metaData;
    }

    @Override
    public boolean hasNext() {
        if (rowIterator == null) {
            rowIterator = new SwiftNode2RowIterator(this);
        }
        return rowIterator.hasNext();
    }

    @Override
    public Row getNextRow() {
        return rowIterator.next();
    }

    @Override
    public void close() {

    }
}
