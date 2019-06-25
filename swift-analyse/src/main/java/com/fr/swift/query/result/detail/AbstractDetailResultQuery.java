package com.fr.swift.query.result.detail;

import com.fr.swift.converter.FindList;
import com.fr.swift.converter.FindListImpl;
import com.fr.swift.query.info.element.target.DetailTarget;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.result.AbstractResultQuery;
import com.fr.swift.result.DetailQueryResultSet;
import com.fr.swift.result.qrs.QueryResultSetMerger;

import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author pony
 * @date 2017/11/27
 */
public abstract class AbstractDetailResultQuery extends AbstractResultQuery<DetailQueryResultSet> {

    private List<DetailTarget> targets;

    AbstractDetailResultQuery(int fetchSize, List<Query<DetailQueryResultSet>> queries) {
        super(fetchSize, queries);
        this.fetchSize = fetchSize;
    }

    AbstractDetailResultQuery(int fetchSize, List<Query<DetailQueryResultSet>> queries, List<DetailTarget> targets) {
        super(fetchSize, queries);
        this.fetchSize = fetchSize;
        this.targets = targets;
    }

    protected abstract QueryResultSetMerger<DetailQueryResultSet> createMerger();

    @Override
    public DetailQueryResultSet getQueryResult() throws SQLException {
        FindList<DetailQueryResultSet> list = new FindListImpl<DetailQueryResultSet>(queryList);
        QueryResultSetMerger<DetailQueryResultSet> merger = createMerger();
        try {
            return merger.merge(list.forEach(new FindList.ConvertEach<Query<DetailQueryResultSet>, DetailQueryResultSet>() {
                @Override
                public DetailQueryResultSet forEach(int idx, Query<DetailQueryResultSet> item) throws Exception {
                    return item.getQueryResult();
                }
            }));
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }
}
