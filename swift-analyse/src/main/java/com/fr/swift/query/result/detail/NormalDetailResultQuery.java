package com.fr.swift.query.result.detail;

import com.fr.swift.query.Query;
import com.fr.swift.query.info.target.DetailTarget;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.result.MultiSegmentDetailResultSet;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author pony
 * @date 2017/11/27
 */
public class NormalDetailResultQuery extends AbstractDetailResultQuery {

    private SwiftMetaData metaData;
    public NormalDetailResultQuery(List<Query<DetailResultSet>> queries, SwiftMetaData metaData) {
        super(queries);
        this.metaData = metaData;
    }

    public NormalDetailResultQuery(List<Query<DetailResultSet>> queries, DetailTarget[] targets, SwiftMetaData metaData) {
        super(queries, targets);
        this.metaData = metaData;
    }

    @Override
    public DetailResultSet getQueryResult() throws SQLException {
        if(queryList.size() == 0) {
            return DetailResultSet.EMPTY;
        }
        if(queryList.size() == 1) {
            return queryList.get(0).getQueryResult();
        }
        return new MultiSegmentDetailResultSet(queryList, metaData);
    }
}
