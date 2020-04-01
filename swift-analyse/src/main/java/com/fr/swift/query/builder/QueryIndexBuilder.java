package com.fr.swift.query.builder;

import com.fr.swift.bitmap.ImmutableBitMap;
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
import com.fr.swift.segment.SegmentKey;

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
public class QueryIndexBuilder extends BaseQueryBuilder {

    public static IndexQuery<ImmutableBitMap> buildQuery(QueryBean bean, SegmentKey segmentKey) {
        QueryInfoBean infoBean = (QueryInfoBean) bean;
        DetailQueryInfo info = (DetailQueryInfo) QueryInfoParser.parse(infoBean);

        Builder builder = new Builder();
        //只计算本地的块
        return builder.buildLocalQuery(info, segmentKey);
    }

    public static Map<SegmentKey, IndexQuery<ImmutableBitMap>> buildQuery(QueryBean bean) throws Exception {
        QueryInfoBean infoBean = (QueryInfoBean) bean;
        DetailQueryInfo info = (DetailQueryInfo) QueryInfoParser.parse(infoBean);
        List<SegmentKey> segmentKeyList = filterQuerySegKeys(info);
        Builder builder = new Builder();
        Map<SegmentKey, IndexQuery<ImmutableBitMap>> queries = new HashMap<>();
        segmentKeyList.forEach(segmentKey -> queries.put(segmentKey, builder.buildLocalQuery(info, segmentKey)));
        return queries;
    }

    static class Builder implements LocalDetailIndexQueryBuilder {
        @Override
        public IndexQuery<ImmutableBitMap> buildLocalQuery(DetailQueryInfo info, SegmentKey segmentKey) {
            List<FilterInfo> filterInfos = new ArrayList<>();
            if (info.getFilterInfo() != null) {
                filterInfos.add(info.getFilterInfo());
            }
            Segment seg = SEG_SVC.getSegment(segmentKey);
            DetailFilter detailFilter = FilterBuilder.buildDetailFilter(seg, new GeneralFilterInfo(filterInfos, GeneralFilterInfo.AND));
            return new LocalIndexQuery(detailFilter.createFilterIndex());
        }
    }
}
