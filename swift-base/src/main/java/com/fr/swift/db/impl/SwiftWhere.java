package com.fr.swift.db.impl;

import com.fr.stable.query.condition.QueryCondition;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.db.Table;
import com.fr.swift.db.Where;
import com.fr.swift.query.query.IndexQuery;
import com.fr.swift.query.query.QueryRunnerProvider;
import com.fr.swift.segment.Segment;

import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * This class created on 2018/7/4
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftWhere implements Where, Serializable {

    private static final long serialVersionUID = 1116521843669790563L;
    private QueryCondition queryCondition;

    public SwiftWhere(QueryCondition queryCondition) {
        this.queryCondition = queryCondition;
    }

    @Override
    public QueryCondition getQueryCondition() {
        return queryCondition;
    }

    @Override
    public ImmutableBitMap createWhereIndex(Table table, Segment segment) throws Exception {
        IndexQuery<ImmutableBitMap> indexAfterFilter = QueryRunnerProvider.getInstance().executeIndexQuery(table, this, segment);
        return indexAfterFilter.getQueryIndex();
    }

    @Override
    public Map<URI, ImmutableBitMap> createWhereIndex(Table table) throws Exception {
        Map<URI, IndexQuery<ImmutableBitMap>> indexAfterFilter = QueryRunnerProvider.getInstance().executeIndexQuery(table, this);
        Map<URI, ImmutableBitMap> whereIndexMap = new HashMap<URI, ImmutableBitMap>();

        for (Map.Entry<URI, IndexQuery<ImmutableBitMap>> entry : indexAfterFilter.entrySet()) {
            whereIndexMap.put(entry.getKey(), entry.getValue().getQueryIndex());
        }
        return whereIndexMap;
    }
}
