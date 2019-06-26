package com.fr.swift.query.result.detail;

import com.fr.swift.query.info.element.target.DetailTarget;
import com.fr.swift.query.query.Query;
import com.fr.swift.result.DetailQueryResultSet;
import com.fr.swift.result.MergeDetailQueryResultSet;

import java.util.List;

/**
 * @author pony
 * @date 2017/11/27
 */
public class NormalDetailResultQuery extends AbstractDetailResultQuery {

    public NormalDetailResultQuery(int fetchSize, List<Query<DetailQueryResultSet>> queries) {
        super(fetchSize, queries);
    }

    public NormalDetailResultQuery(int fetchSize, List<Query<DetailQueryResultSet>> queries, List<DetailTarget> targets) {
        super(fetchSize, queries, targets);
    }

    @Override
    public DetailQueryResultSet merge(List<DetailQueryResultSet> resultSets) {
        return new MergeDetailQueryResultSet(fetchSize, resultSets);
    }
}
