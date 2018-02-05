package com.fr.swift.cal.result.detail;

import com.fr.swift.cal.Query;
import com.fr.swift.query.adapter.target.DetailTarget;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.result.SortMultiSegmentDetailResultSet;
import com.fr.swift.result.SortSegmentDetailResultSet;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

/**
 * Created by pony on 2017/11/27.
 */
public class SortDetailResultQuery extends AbstractDetailResultQuery {
//    private IntList sortIndex;
    private Comparator comparator;

    public SortDetailResultQuery(List<Query<DetailResultSet>> queries) {
        super(queries);

    }

    public SortDetailResultQuery(List<Query<DetailResultSet>> queries, DetailTarget[] targets) {
        super(queries, targets);
    }

    @Override
    public DetailResultSet getQueryResult() throws SQLException {

        comparator = ((SortSegmentDetailResultSet)queryList.get(0).getQueryResult()).getDetailSortComparator();
        return new SortMultiSegmentDetailResultSet(queryList, comparator);
    }
}
