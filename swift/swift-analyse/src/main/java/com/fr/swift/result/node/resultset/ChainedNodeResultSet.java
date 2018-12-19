package com.fr.swift.result.node.resultset;

import com.fr.swift.result.BaseNodeResultSet;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.SwiftNode2RowIterator;
import com.fr.swift.result.SwiftNodeOperator;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.structure.Pair;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Lyon
 * @date 2018/6/12
 */
public class ChainedNodeResultSet extends BaseNodeResultSet<SwiftNode> implements NodeResultSet<SwiftNode> {

    private SwiftNodeOperator operator;

    private NodeResultSet<SwiftNode> source;

    private SwiftMetaData metaData;

    private Iterator<Row> rowIterator;

    public ChainedNodeResultSet(SwiftNodeOperator operator, NodeResultSet<SwiftNode> source) {
        super(source.getFetchSize());
        this.operator = operator;
        this.source = source;
    }

    public ChainedNodeResultSet(SwiftNodeOperator operator, NodeResultSet source, SwiftMetaData metaData) {
        this(operator, source);
        this.metaData = metaData;
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
