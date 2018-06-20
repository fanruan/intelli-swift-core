package com.fr.swift.query.result.detail;

import com.fr.swift.query.info.element.target.DetailTarget;
import com.fr.swift.query.query.Query;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.result.SortMultiSegmentDetailResultSet;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.structure.Pair;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

/**
 * Created by pony on 2017/11/27.
 */
public class SortDetailResultQuery extends AbstractDetailResultQuery {

    private List<Pair<Integer, Comparator>> comparators;
    private SwiftMetaData metaData;

    public SortDetailResultQuery(List<Query<DetailResultSet>> queries, List<Pair<Integer, Comparator>> comparators, SwiftMetaData metaData) {
        super(queries);
        this.comparators = comparators;
        this.metaData = metaData;
    }

    public SortDetailResultQuery(List<Query<DetailResultSet>> queries, List<DetailTarget> targets,
                                 List<Pair<Integer, Comparator>> comparators, SwiftMetaData metaData) {
        super(queries, targets);
        this.comparators = comparators;
        this.metaData = metaData;
    }

    @Override
    public DetailResultSet getQueryResult() throws SQLException {
        return new SortMultiSegmentDetailResultSet(queryList, comparators, metaData);
    }
}
