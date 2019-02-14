package com.fr.swift.result.node.resultset;

import com.fr.swift.compare.Comparators;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeQRS;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.structure.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author anchore
 * @date 2018/12/19
 */
public class NodeQueryResultSetMerger implements INodeQueryResultSetMerger<GroupNode> {

    private static final long serialVersionUID = 4561111090120768112L;
    private int fetchSize;
    private boolean[] isGlobalIndexed;
    private List<Aggregator> aggregators;
    private List<Pair<SortType, ColumnTypeConstants.ClassType>> comparators;

    public NodeQueryResultSetMerger(int fetchSize, boolean[] isGlobalIndexed, List<Aggregator> aggregators,
                                    List<Pair<SortType, ColumnTypeConstants.ClassType>> comparators) {
        this.fetchSize = fetchSize;
        this.isGlobalIndexed = isGlobalIndexed;
        this.aggregators = aggregators;
        this.comparators = comparators;
    }

    @Override
    public NodeMergeQRS<GroupNode> merge(List<NodeMergeQRS<GroupNode>> resultSets) {
        return new ChainedNodeMergeQRS(fetchSize, isGlobalIndexed, resultSets, aggregators, getComparators(), this);
    }

    private List<Comparator<GroupNode>> getComparators() {
        List<Comparator> list = new ArrayList<Comparator>();
        for (Pair<SortType, ColumnTypeConstants.ClassType> pair : comparators) {
            boolean isAsc = pair.getKey() == SortType.ASC;
            switch (pair.getValue()) {
                case DOUBLE:
                    list.add(isAsc ? Comparators.<Double>asc() : Comparators.<Double>desc());
                    break;
                case LONG:
                case DATE:
                    list.add(isAsc ? Comparators.<Long>asc() : Comparators.<Long>desc());
                    break;
                case INTEGER:
                    list.add(isAsc ? Comparators.<Integer>asc() : Comparators.<Integer>desc());
                    break;
                default:
                    list.add(isAsc ? Comparators.STRING_ASC : Comparators.reverse(Comparators.STRING_ASC));
            }
        }
        List<Comparator<GroupNode>> result = new ArrayList<Comparator<GroupNode>>();
        for (final Comparator comparator : list) {
            result.add(new Comparator<GroupNode>() {
                @Override
                public int compare(GroupNode o1, GroupNode o2) {
                    return comparator.compare(o1.getData(), o2.getData());
                }
            });
        }
        return result;
    }
}