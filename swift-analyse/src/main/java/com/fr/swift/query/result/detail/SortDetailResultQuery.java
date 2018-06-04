package com.fr.swift.query.result.detail;

import com.fr.swift.query.Query;
import com.fr.swift.query.info.element.target.DetailTarget;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.result.SortMultiSegmentDetailResultSet;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

/**
 * Created by pony on 2017/11/27.
 */
public class SortDetailResultQuery extends AbstractDetailResultQuery {

    private Comparator comparator;
    private SwiftMetaData metaData;

    public SortDetailResultQuery(List<Query<DetailResultSet>> queries, Comparator comparator, SwiftMetaData metaData) {
        super(queries);
        this.comparator = comparator;
        this.metaData = metaData;
    }

    public SortDetailResultQuery(List<Query<DetailResultSet>> queries, DetailTarget[] targets, Comparator comparator, SwiftMetaData metaData) {
        super(queries, targets);
        this.comparator = comparator;
        this.metaData = metaData;
    }

    @Override
    public DetailResultSet getQueryResult() throws SQLException {

        if(queryList.size() == 0) {
            return null;
        }

        if(queryList.size() == 1) {
            return queryList.get(0).getQueryResult();
        }
        return new SortMultiSegmentDetailResultSet(queryList, comparator, metaData);
    }
}
