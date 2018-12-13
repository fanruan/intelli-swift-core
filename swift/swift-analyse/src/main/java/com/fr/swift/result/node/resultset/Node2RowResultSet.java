package com.fr.swift.result.node.resultset;

import com.fr.swift.result.BaseNodeResultSet;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.structure.Pair;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * node转为row，该resultSet只能作为结果返回给客户端，不再用于处理相关中间结果计算
 * <p>
 * Created by lyon on 2018/8/22.
 */
public class Node2RowResultSet extends BaseNodeResultSet<SwiftNode> implements NodeResultSet<SwiftNode> {

    private NodeResultSet source;
    private SwiftMetaData metaData;

    public Node2RowResultSet(NodeResultSet source, SwiftMetaData metaData) {
        this.source = source;
        this.metaData = metaData;
    }

    @Override
    public Pair<SwiftNode, List<Map<Integer, Object>>> getPage() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasNextPage() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getFetchSize() {
        return source.getFetchSize();
    }

    @Override
    public SwiftMetaData getMetaData() {
        return metaData;
    }

    @Override
    public boolean hasNext() throws SQLException {
        return null != source && source.hasNext();
    }

    @Override
    public Row getNextRow() throws SQLException {
        return source.getNextRow();
    }

    @Override
    public void close() {

    }
}
