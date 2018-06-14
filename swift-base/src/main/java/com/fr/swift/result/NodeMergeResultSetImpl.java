package com.fr.swift.result;

import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.structure.iterator.Tree2RowIterator;
import com.fr.swift.util.function.Function;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/4/27.
 */
public class NodeMergeResultSetImpl<T extends GroupNode> implements NodeMergeResultSet<T> {

    private boolean hasNextPage = true;
    private GroupNode root;
    private List<Map<Integer, Object>> rowGlobalDictionaries;
    private Iterator<List<SwiftNode>> tree2RowIterator;

    public NodeMergeResultSetImpl(GroupNode root, List<Map<Integer, Object>> rowGlobalDictionaries) {
        this.root = root;
        this.rowGlobalDictionaries = rowGlobalDictionaries;
    }

    @Override
    public List<Map<Integer, Object>> getRowGlobalDictionaries() {
        return rowGlobalDictionaries;
    }

    @Override
    public SwiftNode<T> getNode() {
        // 只有一页，适配ChainedResultSet
        hasNextPage = false;
        return root;
    }

    @Override
    public boolean hasNextPage() {
        return hasNextPage;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return null;
    }

    @Override
    public boolean next() throws SQLException {
        if (tree2RowIterator == null) {
            this.tree2RowIterator = new Tree2RowIterator<SwiftNode>(rowGlobalDictionaries.size(), root.getChildren().iterator(), new Function<SwiftNode, Iterator<SwiftNode>>() {
                @Override
                public Iterator<SwiftNode> apply(SwiftNode p) {
                    return p.getChildren().iterator();
                }
            });
        }
        return tree2RowIterator.hasNext();
    }

    @Override
    public Row getRowData() throws SQLException {
        List<SwiftNode> row = tree2RowIterator.next();
        return SwiftNodeUtils.nodes2Row(row);
    }

    @Override
    public void close() throws SQLException {

    }
}
