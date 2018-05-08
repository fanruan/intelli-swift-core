package com.fr.swift.cal.result.detail;

import com.fr.swift.cal.Query;
import com.fr.swift.cal.result.AbstractResultQuery;
import com.fr.swift.query.adapter.target.DetailTarget;
import com.fr.swift.result.DetailResultSet;

import java.util.List;

/**
 * Created by pony on 2017/11/27.
 */
public abstract class AbstractDetailResultQuery extends AbstractResultQuery<DetailResultSet>{
    protected DetailTarget[] targets;

    public AbstractDetailResultQuery(List<Query<DetailResultSet>> queries) {
        super(queries);
    }

    public AbstractDetailResultQuery(List<Query<DetailResultSet>> queries, DetailTarget[] targets) {
        super(queries);
        this.targets = targets;
    }

}
