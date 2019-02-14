package com.fr.swift.service;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.db.Table;
import com.fr.swift.db.Where;
import com.fr.swift.query.builder.QueryIndexBuilder;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.query.DetailQueryInfoBean;
import com.fr.swift.query.query.FilterBean;
import com.fr.swift.query.query.IndexQuery;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.query.QueryIndexRunner;
import com.fr.swift.segment.Segment;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

/**
 * This class created on 2018/7/4
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@SwiftBean(name = "queryIndexRunner")
public class QueryIndexService implements QueryIndexRunner {

    private static QueryBean createQueryBean(Table table, Where where) {
        FilterBean filterBean = where.getFilterBean();
        DetailQueryInfoBean queryInfoBean = new DetailQueryInfoBean();
        queryInfoBean.setQueryId(UUID.randomUUID().toString());
        queryInfoBean.setTableName(table.getSourceKey().getId());
        queryInfoBean.setFilter((FilterInfoBean) filterBean);
        return queryInfoBean;
    }

    @Override
    public Map<URI, IndexQuery<ImmutableBitMap>> getBitMap(Table table, Where where) throws Exception {
        return QueryIndexBuilder.buildQuery(createQueryBean(table, where));
    }

    @Override
    public IndexQuery<ImmutableBitMap> getBitMap(Table table, Where where, Segment segment) {
        return QueryIndexBuilder.buildQuery(createQueryBean(table, where), segment);
    }
}
