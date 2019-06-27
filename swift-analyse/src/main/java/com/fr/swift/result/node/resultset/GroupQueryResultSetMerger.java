package com.fr.swift.result.node.resultset;

import com.fr.swift.compare.Comparators;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.group.by2.node.GroupPage;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.result.qrs.QueryResultSetMerger;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.structure.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author anchore
 * @date 2018/12/19
 */
public class GroupQueryResultSetMerger implements QueryResultSetMerger<QueryResultSet<GroupPage>>, Serializable {

    private static final long serialVersionUID = 4561111090120768112L;
    private int fetchSize;
    private boolean[] isGlobalIndexed;
    private List<Aggregator> aggregators;
    private List<Comparator<SwiftNode>> comparators;

    private GroupQueryResultSetMerger(int fetchSize,
                                      boolean[] isGlobalIndexed,
                                      List<Aggregator> aggregators,
                                      List<Comparator<SwiftNode>> comparators) {
        this.fetchSize = fetchSize;
        this.isGlobalIndexed = isGlobalIndexed;
        this.aggregators = aggregators;
        this.comparators = comparators;
    }

    public static GroupQueryResultSetMerger ofCompareInfo(int fetchSize,
                                                          boolean[] isGlobalIndexed,
                                                          List<Aggregator> aggregators,
                                                          List<Pair<SortType, ColumnTypeConstants.ClassType>> comparators) {
        return ofComparator(fetchSize, isGlobalIndexed, aggregators, getComparators(comparators));
    }

    public static GroupQueryResultSetMerger ofComparator(int fetchSize,
                                                         boolean[] isGlobalIndexed,
                                                         List<Aggregator> aggregators,
                                                         List<Comparator<SwiftNode>> comparators) {
        return new GroupQueryResultSetMerger(fetchSize, isGlobalIndexed, aggregators, comparators);
    }

    private static List<Comparator<SwiftNode>> getComparators(List<Pair<SortType, ColumnTypeConstants.ClassType>> comparators) {
        List<Comparator<?>> list = new ArrayList<Comparator<?>>();
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
        List<Comparator<SwiftNode>> result = new ArrayList<Comparator<SwiftNode>>();
        for (final Comparator comparator : list) {
            result.add(new Comparator<SwiftNode>() {
                @Override
                public int compare(SwiftNode o1, SwiftNode o2) {
                    return comparator.compare(o1.getData(), o2.getData());
                }
            });
        }
        return result;
    }

    @Override
    public QueryResultSet<GroupPage> merge(List<QueryResultSet<GroupPage>> resultSets) {
        return new MergeGroupQueryResultSet(fetchSize, isGlobalIndexed, resultSets, aggregators, comparators);
    }
}