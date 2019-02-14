package com.fr.swift.query.result.detail;

import com.fr.swift.query.info.element.target.DetailTarget;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.SortedDetailResultSetMerger;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.result.qrs.QueryResultSetMerger;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.structure.Pair;

import java.util.List;

/**
 * Created by pony on 2017/11/27.
 */
public class SortDetailResultQuery extends AbstractDetailResultQuery {

    private List<Pair<Sort, ColumnTypeConstants.ClassType>> comparators;

    public SortDetailResultQuery(int fetchSize, List<Query<QueryResultSet>> queries, List<Pair<Sort, ColumnTypeConstants.ClassType>> comparators) {
        super(fetchSize, queries);
        this.comparators = comparators;
    }

    public SortDetailResultQuery(int fetchSize, List<Query<QueryResultSet>> queries, List<DetailTarget> targets,
                                 List<Pair<Sort, ColumnTypeConstants.ClassType>> comparators) {
        super(fetchSize, queries, targets);
        this.comparators = comparators;
    }

    @Override
    protected QueryResultSetMerger createMerger() {
        return new SortedDetailResultSetMerger(fetchSize, comparators);
    }
}
