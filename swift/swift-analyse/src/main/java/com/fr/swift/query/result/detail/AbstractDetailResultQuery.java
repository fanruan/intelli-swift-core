package com.fr.swift.query.result.detail;

import com.fr.swift.query.info.element.target.DetailTarget;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.result.AbstractResultQuery;
import com.fr.swift.result.qrs.QueryResultSet;

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
}
