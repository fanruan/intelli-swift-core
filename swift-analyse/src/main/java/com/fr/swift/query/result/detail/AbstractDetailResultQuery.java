package com.fr.swift.query.result.detail;

import com.fr.swift.query.info.element.target.DetailTarget;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.result.AbstractResultQuery;
import com.fr.swift.result.DetailQueryResultSet;

import java.util.List;

/**
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
}
