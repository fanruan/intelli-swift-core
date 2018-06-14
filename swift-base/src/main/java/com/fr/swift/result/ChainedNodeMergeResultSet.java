package com.fr.swift.result;

import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.function.Function;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/6/14.
 */
public class ChainedNodeMergeResultSet implements NodeMergeResultSet<GroupNode> {

    private List<NodeMergeResultSet<GroupNode>> sources;
    private Function<List<NodeMergeResultSet<GroupNode>>, NodeMergeResultSet<GroupNode>> operator;
    private List<Map<Integer, Object>> totalDictionaries;

    public ChainedNodeMergeResultSet(List<NodeMergeResultSet<GroupNode>> sources,
                                     Function<List<NodeMergeResultSet<GroupNode>>, NodeMergeResultSet<GroupNode>> operator) {
        this.sources = sources;
        this.operator = operator;
    }

    @Override
    public List<Map<Integer, Object>> getRowGlobalDictionaries() {
        return totalDictionaries;
    }

    @Override
    public SwiftNode<GroupNode> getNode() {
        if (!hasNextPage()) {
            return null;
        }
        List<NodeMergeResultSet<GroupNode>> resultSets = new ArrayList<NodeMergeResultSet<GroupNode>>();
        for (NodeMergeResultSet<GroupNode> source : sources) {
            if (source.hasNextPage()) {
                resultSets.add(new NodeMergeResultSetImpl<GroupNode>((GroupNode) source.getNode(), source.getRowGlobalDictionaries()));
            }
        }
        NodeMergeResultSet<GroupNode> mergeResultSet = operator.apply(resultSets);
        // TODO: 2018/6/14 这个字典合并的只能先调用getNode再取字典map
        totalDictionaries = mergeResultSet.getRowGlobalDictionaries();
        return mergeResultSet.getNode();
    }

    @Override
    public boolean hasNextPage() {
        for (NodeMergeResultSet<GroupNode> source : sources) {
            if (source.hasNextPage()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return null;
    }

    @Override
    public boolean next() throws SQLException {
        return false;
    }

    @Override
    public Row getRowData() throws SQLException {
        return null;
    }

    @Override
    public void close() throws SQLException {

    }
}
