package com.fr.swift.query.builder;

import com.fr.swift.SwiftContext;
import com.fr.swift.query.filter.FilterBuilder;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.group.info.IndexInfo;
import com.fr.swift.query.info.detail.DetailQueryInfo;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.result.detail.NormalDetailResultQuery;
import com.fr.swift.query.segment.detail.NormalDetailSegmentQuery;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pony
 * @date 2017/12/14
 */
public class LocalDetailNormalQueryBuilder implements LocalDetailQueryBuilder {

    private final SwiftSegmentManager localSegmentProvider = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);

    protected LocalDetailNormalQueryBuilder() {
    }

    @Override
    public Query<QueryResultSet> buildLocalQuery(DetailQueryInfo info) {
        List<Query<QueryResultSet>> queries = new ArrayList<Query<QueryResultSet>>();
        List<Segment> segments = localSegmentProvider.getSegmentsByIds(info.getTable(), info.getQuerySegment());
        for (Segment segment : segments) {
            List<FilterInfo> filterInfos = new ArrayList<FilterInfo>();
            List<Pair<Column, IndexInfo>> columns = AbstractLocalGroupQueryBuilder.getDimensionSegments(segment, info.getDimensions());
            if (info.getFilterInfo() != null) {
                filterInfos.add(info.getFilterInfo());
            }
            queries.add(new NormalDetailSegmentQuery(info.getFetchSize(), columns,
                    FilterBuilder.buildDetailFilter(segment, new GeneralFilterInfo(filterInfos, GeneralFilterInfo.AND))));
        }
        return new NormalDetailResultQuery(info.getFetchSize(), queries);
    }
}
