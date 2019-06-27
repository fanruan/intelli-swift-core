package com.fr.swift.result;

import com.fr.swift.compare.Comparators;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.result.qrs.QueryResultSetMerger;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.Row;
import com.fr.swift.structure.Pair;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author yee
 * @date 2018-12-17
 */
public class SortedDetailQueryResultSetMerger implements QueryResultSetMerger<DetailQueryResultSet>, Serializable {

    private static final long serialVersionUID = -6390000892109475367L;
    private int fetchSize;
    private Comparator<Row> comparators;

    private SortedDetailQueryResultSetMerger(int fetchSize, Comparator<Row> comparators) {
        this.fetchSize = fetchSize;
        this.comparators = comparators;
    }

    public static SortedDetailQueryResultSetMerger ofCompareInfo(int fetchSize,
                                                                 List<Pair<Sort, ColumnTypeConstants.ClassType>> comparators) {
        return ofComparator(fetchSize, createRowComparator(comparators));
    }

    public static SortedDetailQueryResultSetMerger ofComparator(int fetchSize,
                                                                Comparator<Row> comparators) {
        return new SortedDetailQueryResultSetMerger(fetchSize, comparators);
    }

    @Override
    public DetailQueryResultSet merge(List<DetailQueryResultSet> queryResultSets) {
        return new MergeSortedDetailQueryResultSet(fetchSize, comparators, queryResultSets);
    }

    private static Comparator<Row> createRowComparator(final List<Pair<Sort, ColumnTypeConstants.ClassType>> comparators) {
        Collections.sort(comparators, new Comparator<Pair<Sort, ColumnTypeConstants.ClassType>>() {
            @Override
            public int compare(Pair<Sort, ColumnTypeConstants.ClassType> o1, Pair<Sort, ColumnTypeConstants.ClassType> o2) {
                return o1.getKey().getTargetIndex() - o2.getKey().getTargetIndex();
            }
        });
        return new Comparator<Row>() {
            @Override
            public int compare(Row o1, Row o2) {
                for (Pair<Sort, ColumnTypeConstants.ClassType> pair : comparators) {
                    // TODO: 2019/6/10 anchore 每次compare都get一组comparator不会慢吗
                    Comparator comparator = getComparator(pair);
                    int result = comparator.compare(o1.getValue(pair.getKey().getTargetIndex()), o2.getValue(pair.getKey().getTargetIndex()));
                    if (result != 0) {
                        return result;
                    }
                }
                return 0;
            }
        };
    }

    private static Comparator<?> getComparator(Pair<Sort, ColumnTypeConstants.ClassType> pair) {
        SortType sortType = pair.getKey().getSortType();
        switch (pair.getValue()) {
            case INTEGER:
                return sortType == SortType.ASC ? Comparators.<Integer>asc() : Comparators.<Integer>desc();
            case LONG:
            case DATE:
                return sortType == SortType.ASC ? Comparators.<Long>asc() : Comparators.<Long>desc();
            case DOUBLE:
                return sortType == SortType.ASC ? Comparators.<Double>asc() : Comparators.<Double>desc();
            default:
                return sortType == SortType.ASC ? Comparators.<String>asc() : Comparators.<String>desc();
        }
    }
}
