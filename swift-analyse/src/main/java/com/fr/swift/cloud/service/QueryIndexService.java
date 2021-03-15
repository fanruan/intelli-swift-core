package com.fr.swift.cloud.service;

import com.fr.swift.cloud.beans.annotation.SwiftBean;
import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.db.Table;
import com.fr.swift.cloud.db.Where;
import com.fr.swift.cloud.query.builder.QueryIndexBuilder;
import com.fr.swift.cloud.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.cloud.query.info.bean.query.DetailQueryInfoBean;
import com.fr.swift.cloud.query.query.FilterBean;
import com.fr.swift.cloud.query.query.IndexQuery;
import com.fr.swift.cloud.query.query.QueryBean;
import com.fr.swift.cloud.query.query.QueryIndexRunner;
import com.fr.swift.cloud.segment.SegmentKey;

import java.util.Collection;
import java.util.Map;

/**
 * This class created on 2018/7/4
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@SwiftBean(name = "queryIndexRunner")
public class QueryIndexService implements QueryIndexRunner {

    private QueryBean createQueryBean(Table table, Where where) {
        FilterBean filterBean = where.getFilterBean();
        return DetailQueryInfoBean.builder(table.getSourceKey().getId()).setFilter((FilterInfoBean) filterBean).build();
    }

    @Override
    public Map<SegmentKey, IndexQuery<ImmutableBitMap>> getBitMap(Table table, Where where) throws Exception {
        return QueryIndexBuilder.buildQuery(createQueryBean(table, where));
    }

    @Override
    public IndexQuery<ImmutableBitMap> getBitMap(Table table, Where where, SegmentKey segmentKey) {
        return QueryIndexBuilder.buildQuery(createQueryBean(table, where), segmentKey);
    }

    @Override
    public Collection<SegmentKey> getWhereSegments(Table table, Where where) throws Exception {
        return QueryIndexBuilder.buildSegments(createQueryBean(table, where));
    }
}
