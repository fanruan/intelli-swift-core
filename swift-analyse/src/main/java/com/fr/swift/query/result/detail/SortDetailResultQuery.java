package com.fr.swift.query.result.detail;

import com.fr.swift.query.info.element.target.DetailTarget;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.result.SortMultiSegmentDetailResultSet;
import com.fr.swift.structure.Pair;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

/**
 * Created by pony on 2017/11/27.
 */
public class SortDetailResultQuery extends AbstractDetailResultQuery {

    private List<Pair<Sort, Comparator>> comparators;

    public SortDetailResultQuery(int fetchSize, List<Query<DetailResultSet>> queries, List<Pair<Sort, Comparator>> comparators) {
        super(fetchSize, queries);
        this.comparators = comparators;
    }

    public SortDetailResultQuery(int fetchSize, List<Query<DetailResultSet>> queries, List<DetailTarget> targets,
                                 List<Pair<Sort, Comparator>> comparators) {
        super(fetchSize, queries, targets);
        this.comparators = comparators;
    }

    @Override
    public DetailResultSet getQueryResult() throws SQLException {
        return new SortMultiSegmentDetailResultSet(fetchSize, queryList, comparators);
    }
}
