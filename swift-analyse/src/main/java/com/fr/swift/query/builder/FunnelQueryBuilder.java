package com.fr.swift.query.builder;

import com.fr.swift.SwiftContext;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.info.bean.parser.QueryInfoParser;
import com.fr.swift.query.info.bean.parser.funnel.FunnelQueryInfo;
import com.fr.swift.query.info.bean.query.FunnelQueryBean;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.result.group.FunnelResultQuery;
import com.fr.swift.query.segment.group.FunnelSegmentQuery;
import com.fr.swift.result.funnel.FunnelQueryResultSet;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.source.SourceKey;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public class FunnelQueryBuilder {


    static Query<QueryResultSet> buildQuery(FunnelQueryBean bean) throws Exception {
        FunnelQueryInfo info = (FunnelQueryInfo) QueryInfoParser.parse(bean);
        SwiftSegmentManager localSegmentProvider = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);
        List<Segment> Segments = localSegmentProvider.getSegment(new SourceKey(bean.getTableName()));
        List<Query<FunnelQueryResultSet>> queries = new ArrayList<Query<FunnelQueryResultSet>>();
        for (Segment segment : Segments) {
            queries.add(new FunnelSegmentQuery(segment, info.getQueryBean()));
        }
        SwiftLoggers.getLogger().debug("number of segment queries: {}", queries.size());
        return new FunnelResultQuery(bean.getAggregation().getFunnelEvents().size(), queries);
    }
}
