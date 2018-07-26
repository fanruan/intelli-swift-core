package com.fr.swift.result.node;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.result.group.GroupNodeMergeUtils;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeResultSet;
import com.fr.swift.result.NodeMergeResultSetImpl;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.function.Function;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/6/14.
 */
public class ChainedNodeMergeResultSet implements NodeMergeResultSet<GroupNode> {

    private int fetchSize;
    private List<NodeMergeResultSet<GroupNode>> sources;
    private Function<List<NodeMergeResultSet<GroupNode>>, NodeMergeResultSet<GroupNode>> operator;
    private List<Map<Integer, Object>> totalDictionaries;

    public ChainedNodeMergeResultSet(int fetchSize, List<NodeMergeResultSet<GroupNode>> sources, List<Aggregator> aggregators,
                                     List<Comparator<GroupNode>> comparators) {
        this.fetchSize = fetchSize;
        this.sources = sources;
        this.operator = new MergeOperator(fetchSize, aggregators, comparators);
    }

    @Override
    public List<Map<Integer, Object>> getRowGlobalDictionaries() {
        return totalDictionaries;
    }

    @Override
    public int getFetchSize() {
        return fetchSize;
    }

    @Override
    public SwiftNode<GroupNode> getNode() {
        if (!hasNextPage()) {
            return null;
        }
        List<NodeMergeResultSet<GroupNode>> resultSets = new ArrayList<NodeMergeResultSet<GroupNode>>();
        for (NodeMergeResultSet<GroupNode> source : sources) {
            if (source.hasNextPage()) {
                resultSets.add(new NodeMergeResultSetImpl<GroupNode>(fetchSize, (GroupNode) source.getNode(), source.getRowGlobalDictionaries()));
            }
        }
        // TODO: 2018/7/26 按需合并
        NodeMergeResultSet<GroupNode> mergeResultSet = operator.apply(resultSets);
        // TODO: 2018/6/14 这个字典合并的只能先调用getNode再取字典map
        SwiftNode node = mergeResultSet.getNode();
        totalDictionaries = mergeResultSet.getRowGlobalDictionaries();
        return node;
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
    public SwiftMetaData getMetaData() {
        return null;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public Row getNextRow() {
        return null;
    }

    @Override
    public void close() {

    }

    private static class MergeOperator implements Function<List<NodeMergeResultSet<GroupNode>>, NodeMergeResultSet<GroupNode>> {

        private int fetchSize;
        private List<Aggregator> aggregators;
        private List<Comparator<GroupNode>> comparators;

        public MergeOperator(int fetchSize, List<Aggregator> aggregators, List<Comparator<GroupNode>> comparators) {
            this.fetchSize = fetchSize;
            this.aggregators = aggregators;
            this.comparators = comparators;
        }

        @Override
        public NodeMergeResultSet<GroupNode> apply(List<NodeMergeResultSet<GroupNode>> groupByResultSets) {
            List<GroupNode> roots = new ArrayList<GroupNode>();
            List<Map<Integer, Object>> totalDictionaries = new ArrayList<Map<Integer, Object>>();
            for (NodeResultSet resultSet : groupByResultSets) {
                roots.add((GroupNode) resultSet.getNode());
                addDictionaries(((NodeMergeResultSet) resultSet).getRowGlobalDictionaries(), totalDictionaries);
            }
            GroupNode mergeNode = GroupNodeMergeUtils.merge(roots, comparators, aggregators);
            return new NodeMergeResultSetImpl<GroupNode>(fetchSize, mergeNode, totalDictionaries);
        }

        private void addDictionaries(List<Map<Integer, Object>> dictionaries,
                                     List<Map<Integer, Object>> totalDictionaries) {
            if (totalDictionaries.size() == 0) {
                for (int i = 0; i < dictionaries.size(); i++) {
                    totalDictionaries.add(new HashMap<Integer, Object>());
                }
            }
            for (int i = 0; i < dictionaries.size(); i++) {
                totalDictionaries.get(i).putAll(dictionaries.get(i));
            }
        }
    }
}
