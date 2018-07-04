package com.fr.swift.service;

import com.fr.stable.query.condition.QueryCondition;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.db.Table;
import com.fr.swift.db.Where;
import com.fr.swift.query.QueryConditionAdaptor;
import com.fr.swift.query.builder.QueryIndexBuilder;
import com.fr.swift.query.query.IndexQuery;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.query.QueryIndexRunner;
import com.fr.swift.segment.Segment;

import java.net.URI;
import java.util.Map;

/**
 * This class created on 2018/7/4
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class QueryIndexService implements QueryIndexRunner {

    @Override
    public Map<URI, IndexQuery<ImmutableBitMap>> getBitMap(Table table, Where where) throws Exception {
        QueryCondition queryCondition = where.getQueryCondition();
        QueryBean queryBean = QueryConditionAdaptor.adaptCondition(queryCondition, table);
        return QueryIndexBuilder.buildQuery(queryBean);
    }

    @Override
    public IndexQuery<ImmutableBitMap> getBitMap(Table table, Where where, Segment segment) throws Exception {
        QueryCondition queryCondition = where.getQueryCondition();
        QueryBean queryBean = QueryConditionAdaptor.adaptCondition(queryCondition, table);
        return QueryIndexBuilder.buildQuery(queryBean, segment);
    }
}
