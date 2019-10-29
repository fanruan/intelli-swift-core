package com.fr.swift.query.result.group;

import com.fr.swift.compare.Comparators;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.group.by2.node.GroupPage;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.result.AbstractResultQuery;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.node.resultset.MergeGroupQueryResultSet;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.structure.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author pony
 * @date 2017/11/27
 */
public class GroupResultQuery extends AbstractResultQuery<QueryResultSet<GroupPage>> {
    private List<Aggregator> aggregators;

    private boolean[] isGlobalIndexed;

    private List<Pair<SortType, ColumnTypeConstants.ClassType>> comparators;

    public GroupResultQuery(int fetchSize, List<Query<QueryResultSet<GroupPage>>> queries, List<Aggregator> aggregators,
                            List<Pair<SortType, ColumnTypeConstants.ClassType>> comparators, boolean[] isGlobalIndexed) {
        super(fetchSize, queries);
        this.aggregators = aggregators;
        this.comparators = comparators;
        this.isGlobalIndexed = isGlobalIndexed;
    }

    @Override
    public QueryResultSet<GroupPage> merge(List<QueryResultSet<GroupPage>> resultSets) {
        return new MergeGroupQueryResultSet(fetchSize, isGlobalIndexed, resultSets, aggregators, getComparators(comparators));
    }

    private static List<Comparator<SwiftNode>> getComparators(List<Pair<SortType, ColumnTypeConstants.ClassType>> comparators) {
        List<Comparator<?>> list = new ArrayList<Comparator<?>>();
        for (Pair<SortType, ColumnTypeConstants.ClassType> pair : comparators) {
            boolean isAsc = pair.getKey() == SortType.ASC;
            switch (pair.getValue()) {
                case ClassType.DOUBLE:
                case ClassType.LONG:
                case ClassType.DATE:
                case ClassType.INTEGER:
                    list.add(isAsc ? Comparators.asc() : Comparators.desc());
                    break;
                case ClassType.STRING:
                    list.add(isAsc ? Comparators.STRING_ASC : Comparators.reverse(Comparators.STRING_ASC));
                    break;
                default:
                    throw new IllegalArgumentException(String.format("unsupported type %s", pair.getValue()));
            }
        }
        List<Comparator<SwiftNode>> result = new ArrayList<Comparator<SwiftNode>>();
        for (final Comparator comparator : list) {
            result.add(new SwiftNodeDataComparator(comparator));
        }
        return result;
    }

    private static class SwiftNodeDataComparator implements Comparator<SwiftNode>, Serializable {
        private static final long serialVersionUID = 4839755197721755766L;
        private final Comparator<Object> comparator;

        SwiftNodeDataComparator(Comparator<Object> comparator) {
            this.comparator = comparator;
        }

        @Override
        public int compare(SwiftNode o1, SwiftNode o2) {
            return comparator.compare(o1.getData(), o2.getData());
        }
    }
}