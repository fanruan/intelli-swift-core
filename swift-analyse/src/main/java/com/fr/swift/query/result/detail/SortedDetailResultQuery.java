package com.fr.swift.query.result.detail;

import com.fr.swift.compare.Comparators;
import com.fr.swift.query.info.element.target.DetailTarget;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.result.DetailQueryResultSet;
import com.fr.swift.result.detail.MergeSortedDetailQueryResultSet;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.ColumnTypeConstants.ClassType;
import com.fr.swift.source.Row;
import com.fr.swift.structure.Pair;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author pony
 * @date 2017/11/27
 */
public class SortedDetailResultQuery extends AbstractDetailResultQuery {

    private List<Pair<Sort, ColumnTypeConstants.ClassType>> comparators;

    public SortedDetailResultQuery(int fetchSize, List<Query<DetailQueryResultSet>> queries, List<Pair<Sort, ColumnTypeConstants.ClassType>> comparators) {
        super(fetchSize, queries);
        this.comparators = comparators;
    }

    public SortedDetailResultQuery(int fetchSize, List<Query<DetailQueryResultSet>> queries, List<DetailTarget> targets,
                                   List<Pair<Sort, ColumnTypeConstants.ClassType>> comparators) {
        super(fetchSize, queries, targets);
        this.comparators = comparators;
    }

    @Override
    public DetailQueryResultSet merge(List<DetailQueryResultSet> queryResultSets) {
        return new MergeSortedDetailQueryResultSet(fetchSize, createRowComparator(comparators), queryResultSets);
    }

    private static Comparator<Row> createRowComparator(List<Pair<Sort, ColumnTypeConstants.ClassType>> comparatorInfos) {
        Collections.sort(comparatorInfos, new Comparator<Pair<Sort, ColumnTypeConstants.ClassType>>() {
            @Override
            public int compare(Pair<Sort, ColumnTypeConstants.ClassType> o1, Pair<Sort, ColumnTypeConstants.ClassType> o2) {
                return o1.getKey().getTargetIndex() - o2.getKey().getTargetIndex();
            }
        });
        final Map<Integer, Comparator> comparators = new HashMap<Integer, Comparator>(comparatorInfos.size());
        for (Pair<Sort, ClassType> pair : comparatorInfos) {
            comparators.put(pair.getKey().getTargetIndex(), getComparator(pair));
        }
        return new Comparator<Row>() {
            @Override
            public int compare(Row o1, Row o2) {
                for (Entry<Integer, Comparator> entry : comparators.entrySet()) {
                    int result = entry.getValue().compare(o1.getValue(entry.getKey()), o2.getValue(entry.getKey()));
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
            case LONG:
            case DATE:
            case DOUBLE:
                return sortType == SortType.ASC ? Comparators.asc() : Comparators.desc();
            case STRING:
                return sortType == SortType.ASC ? Comparators.STRING_ASC : Comparators.reverse(Comparators.STRING_ASC);
            default:
                throw new IllegalStateException(String.format("unsupported type %s", pair.getValue()));
        }
    }
}
