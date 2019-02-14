package com.fr.swift.query.result.detail;

import com.fr.swift.converter.FindList;
import com.fr.swift.converter.FindListImpl;
import com.fr.swift.query.info.element.target.DetailTarget;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.result.AbstractResultQuery;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.result.qrs.QueryResultSetMerger;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by pony on 2017/11/27.
 */
public abstract class AbstractDetailResultQuery extends AbstractResultQuery<QueryResultSet> {

    protected List<DetailTarget> targets;

    public AbstractDetailResultQuery(int fetchSize, List<Query<QueryResultSet>> queries) {
        super(fetchSize, queries);
        this.fetchSize = fetchSize;
    }

    public AbstractDetailResultQuery(int fetchSize, List<Query<QueryResultSet>> queries, List<DetailTarget> targets) {
        super(fetchSize, queries);
        this.fetchSize = fetchSize;
        this.targets = targets;
    }

    protected abstract QueryResultSetMerger createMerger();

    @Override
    public QueryResultSet getQueryResult() throws SQLException {
        FindList<QueryResultSet> list = new FindListImpl<QueryResultSet>(queryList);
        QueryResultSetMerger merger = createMerger();
        try {
            return merger.merge(list.forEach(new FindList.ConvertEach<Query, QueryResultSet>() {
                @Override
                public QueryResultSet forEach(int idx, Query item) throws Exception {
                    return item.getQueryResult();
                }
            }));
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }
}
