package com.fr.swift.result;

import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.structure.iterator.Tree2RowIterator;
import com.fr.swift.util.function.Function;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by pony on 2018/4/19.
 */
public class NodeResultSetImpl<T extends SwiftNode> implements NodeResultSet {

    private SwiftNode node;
    private SwiftMetaData metaData;
    private Iterator<List<SwiftNode>> iterator;

    public NodeResultSetImpl(SwiftNode node) {
        this.node = node;
    }

    public NodeResultSetImpl(SwiftNode node, SwiftMetaData metaData) {
        this.node = node;
        this.metaData = metaData;
    }

    @Override
    public SwiftNode<T> getNode() {
        return node;
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
    public boolean next() throws SQLException {
        if (iterator == null) {
            this.iterator = new Tree2RowIterator<SwiftNode>(SwiftNodeUtils.getDimensionSize(node), node.getChildren().iterator(), new Function<SwiftNode, Iterator<SwiftNode>>() {
                @Override
                public Iterator<SwiftNode> apply(SwiftNode p) {
                    return p.getChildren().iterator();
                }
            });
        }
        return iterator.hasNext();
    }

    @Override
    public Row getRowData() throws SQLException {
        return SwiftNodeUtils.nodes2Row(iterator.next());
    }

    @Override
    public void close() throws SQLException {

    }
}
