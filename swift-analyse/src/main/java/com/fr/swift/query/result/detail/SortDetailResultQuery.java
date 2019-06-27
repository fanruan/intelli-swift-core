package com.fr.swift.query.result.detail;

import com.fr.swift.compare.Comparators;
import com.fr.swift.query.info.element.target.DetailTarget;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.result.DetailQueryResultSet;
import com.fr.swift.result.MergeSortedDetailQueryResultSet;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.Row;
import com.fr.swift.structure.Pair;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author pony
 * @date 2017/11/27
 */
public class SortDetailResultQuery extends AbstractDetailResultQuery {

    private List<Pair<Sort, ColumnTypeConstants.ClassType>> comparators;

    public SortDetailResultQuery(int fetchSize, List<Query<DetailQueryResultSet>> queries, List<Pair<Sort, ColumnTypeConstants.ClassType>> comparators) {
        super(fetchSize, queries);
        this.comparators = comparators;
    }

    public SortDetailResultQuery(int fetchSize, List<Query<DetailQueryResultSet>> queries, List<DetailTarget> targets,
                                 List<Pair<Sort, ColumnTypeConstants.ClassType>> comparators) {
        super(fetchSize, queries, targets);
        this.comparators = comparators;
    }

    @Override
    public DetailQueryResultSet merge(List<DetailQueryResultSet> queryResultSets) {
        return new MergeSortedDetailQueryResultSet(fetchSize, createRowComparator(comparators), queryResultSets);
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
