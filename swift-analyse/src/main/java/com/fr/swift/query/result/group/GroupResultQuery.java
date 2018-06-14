package com.fr.swift.query.result.group;

import com.fr.swift.query.Query;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.result.ChainedNodeMergeResultSet;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeResultSet;
import com.fr.swift.result.NodeMergeResultSetImpl;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.structure.iterator.IteratorUtils;
import com.fr.swift.structure.iterator.MapperIterator;
import com.fr.swift.util.function.Function;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pony on 2017/11/27.
 */
public class GroupResultQuery extends AbstractGroupResultQuery {

    public GroupResultQuery(List<Query<NodeResultSet>> queries, List<Aggregator> aggregators,
                            List<Comparator<Integer>> comparators) {
        super(queries, aggregators, comparators);
    }

    @Override
    public NodeResultSet getQueryResult() throws SQLException {
        List<NodeMergeResultSet<GroupNode>> resultSets = new ArrayList<NodeMergeResultSet<GroupNode>>();
        for (Query<NodeResultSet> query : queryList) {
            resultSets.add((NodeMergeResultSet<GroupNode>) query.getQueryResult());
        }
        Function<List<NodeMergeResultSet<GroupNode>>, NodeMergeResultSet<GroupNode>> operator = new Function<List<NodeMergeResultSet<GroupNode>>, NodeMergeResultSet<GroupNode>>() {
            @Override
            public NodeMergeResultSet<GroupNode> apply(List<NodeMergeResultSet<GroupNode>> groupByResultSets) {
                List<GroupNode> roots = new ArrayList<GroupNode>();
                List<Map<Integer, Object>> totalDictionaries = new ArrayList<Map<Integer, Object>>();
                for (NodeResultSet resultSet : groupByResultSets) {
                    roots.add((GroupNode) resultSet.getNode());
                    addDictionaries(((NodeMergeResultSet) resultSet).getRowGlobalDictionaries(), totalDictionaries);
                }
                GroupNode mergeNode = GroupNodeMergeUtils.merge(roots, nodeComparators(), aggregators);
                return new NodeMergeResultSetImpl<GroupNode>(mergeNode, totalDictionaries);
            }
        };
        // TODO: 2018/6/14 这边仅仅是串联起来了，有待分析改进
        return new ChainedNodeMergeResultSet(resultSets, operator);
    }

    static void addDictionaries(List<Map<Integer, Object>> dictionaries,
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

    private List<Comparator<GroupNode>> nodeComparators() {
        return IteratorUtils.iterator2List(new MapperIterator<Comparator<Integer>, Comparator<GroupNode>>(comparators.iterator(), new Function<Comparator<Integer>, Comparator<GroupNode>>() {
            @Override
            public Comparator<GroupNode> apply(final Comparator<Integer> p) {
                return new Comparator<GroupNode>() {
                    @Override
                    public int compare(GroupNode o1, GroupNode o2) {
                        return p.compare(o1.getDictionaryIndex(), o2.getDictionaryIndex());
                    }
                };
            }
        }));
    }

}