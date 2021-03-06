package com.fr.swift.cloud.db.impl;

import com.fr.swift.cloud.base.json.JsonBuilder;
import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.db.Table;
import com.fr.swift.cloud.db.Where;
import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.query.QueryRunnerProvider;
import com.fr.swift.cloud.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.cloud.query.query.FilterBean;
import com.fr.swift.cloud.query.query.IndexQuery;
import com.fr.swift.cloud.segment.SegmentKey;
import com.fr.swift.cloud.util.Strings;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class created on 2018/7/4
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftWhere implements Where {

    private static final long serialVersionUID = 1116521843669790563L;

    private transient FilterBean filterBean;
    private String filterBeanJson;

    public SwiftWhere(FilterBean filterBean) {
        this.filterBean = filterBean;
        try {
            this.filterBeanJson = JsonBuilder.writeJsonString(filterBean);
        } catch (Exception e) {
            SwiftLoggers.getLogger().warn("build filter bean json failed", e);
        }

    }

    @Override
    public FilterBean getFilterBean() {
        if (null == filterBean && Strings.isNotEmpty(filterBeanJson)) {
            try {
                filterBean = JsonBuilder.readValue(filterBeanJson, FilterInfoBean.class);
            } catch (Exception e) {
                SwiftLoggers.getLogger().error("build filter bean failed", e);
            }
        }
        return filterBean;
    }

    @Override
    public ImmutableBitMap createWhereIndex(Table table, SegmentKey segmentKey) throws Exception {
        IndexQuery<ImmutableBitMap> indexAfterFilter = QueryRunnerProvider.getInstance().executeIndexQuery(table, this, segmentKey);
        return indexAfterFilter.getQueryIndex();
    }

    @Override
    public Map<SegmentKey, ImmutableBitMap> createWhereIndex(Table table) throws Exception {
        Map<SegmentKey, IndexQuery<ImmutableBitMap>> indexAfterFilter = QueryRunnerProvider.getInstance().executeIndexQuery(table, this);
        Map<SegmentKey, ImmutableBitMap> whereIndexMap = new HashMap<>();
        indexAfterFilter.forEach((segmentKey, indexQuery) -> whereIndexMap.put(segmentKey, indexQuery.getQueryIndex()));
        return whereIndexMap;
    }

    @Override
    public Collection<SegmentKey> createWhereSegments(Table table) throws Exception {
        return QueryRunnerProvider.getInstance().executeSegmentsQuery(table,this);
    }
}
