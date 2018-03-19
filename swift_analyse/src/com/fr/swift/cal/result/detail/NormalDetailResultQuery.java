package com.fr.swift.cal.result.detail;

import com.fr.swift.cal.Query;
import com.fr.swift.query.adapter.target.DetailTarget;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.result.MultiSegmentDetailResultSet;

import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author pony
 * @date 2017/11/27
 */
public class NormalDetailResultQuery extends AbstractDetailResultQuery {
    public NormalDetailResultQuery(List<Query<DetailResultSet>> queries) {
        super(queries);
    }

    public NormalDetailResultQuery(List<Query<DetailResultSet>> queries, DetailTarget[] targets) {
        super(queries, targets);

    }

    @Override
    public DetailResultSet getQueryResult() throws SQLException {
        return new MultiSegmentDetailResultSet(queryList);
    }
}
