package com.fr.swift.query.builder;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.SwiftContext;
import com.fr.swift.exception.SwiftSegmentAbsentException;
import com.fr.swift.query.filter.FilterBuilder;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.info.bean.parser.QueryInfoParser;
import com.fr.swift.query.info.bean.query.QueryInfoBean;
import com.fr.swift.query.info.detail.DetailQueryInfo;
import com.fr.swift.query.query.IndexQuery;
import com.fr.swift.query.query.LocalIndexQuery;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentLocationProvider;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.source.SourceKey;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2018/7/4
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class QueryIndexBuilder {

    public static IndexQuery<ImmutableBitMap> buildQuery(QueryBean bean, Segment segment) {
        QueryInfoBean infoBean = (QueryInfoBean) bean;
        DetailQueryInfo info = (DetailQueryInfo) QueryInfoParser.parse(infoBean);

        Builder builder = new Builder();
        //只计算本地的块
        return builder.buildLocalQuery(info, segment);
    }

    public static Map<URI, IndexQuery<ImmutableBitMap>> buildQuery(QueryBean bean) throws Exception {
        QueryInfoBean infoBean = (QueryInfoBean) bean;
        DetailQueryInfo info = (DetailQueryInfo) QueryInfoParser.parse(infoBean);

        SourceKey table = info.getTable();
        List<SegmentDestination> uris = SegmentLocationProvider.getInstance().getSegmentLocationURI(table);
        if (uris == null || uris.isEmpty()) {
            throw new SwiftSegmentAbsentException("no such table");
        }
        Builder builder = new Builder();
        //只计算本地的块
        Map<URI, IndexQuery<ImmutableBitMap>> queries = new HashMap<URI, IndexQuery<ImmutableBitMap>>();
        for (SegmentDestination uri : uris) {
            if (!uri.isRemote()) {
                queries.putAll(builder.buildLocalQuery(info));
            }
        }
        return queries;
    }

    static class Builder implements LocalDetailIndexQueryBuilder {

        private final SwiftSegmentManager localSegmentProvider = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);

        @Override
        public Map<URI, IndexQuery<ImmutableBitMap>> buildLocalQuery(DetailQueryInfo info) {
            Map<URI, IndexQuery<ImmutableBitMap>> queries = new HashMap<URI, IndexQuery<ImmutableBitMap>>();
            List<Segment> segments = localSegmentProvider.getSegmentsByIds(info.getTable(), info.getQuerySegment());
            for (Segment segment : segments) {
                queries.put(segment.getLocation().getUri(), buildLocalQuery(info, segment));
            }
            return queries;
        }

        @Override
        public IndexQuery<ImmutableBitMap> buildLocalQuery(DetailQueryInfo info, Segment segment) {
            List<FilterInfo> filterInfos = new ArrayList<FilterInfo>();
            if (info.getFilterInfo() != null) {
                filterInfos.add(info.getFilterInfo());
            }
            DetailFilter detailFilter = FilterBuilder.buildDetailFilter(segment, new GeneralFilterInfo(filterInfos, GeneralFilterInfo.AND));
            return new LocalIndexQuery(detailFilter.createFilterIndex());
        }
    }
}
