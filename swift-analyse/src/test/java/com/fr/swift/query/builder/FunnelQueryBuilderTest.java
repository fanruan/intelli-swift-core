package com.fr.swift.query.builder;

import com.fr.swift.SwiftContext;
import com.fr.swift.query.info.bean.parser.QueryInfoParser;
import com.fr.swift.query.info.bean.parser.funnel.FunnelQueryInfo;
import com.fr.swift.query.info.bean.query.FunnelQueryBean;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.result.group.FunnelResultQuery;
import com.fr.swift.query.segment.group.FunnelSegmentQuery;
import com.fr.swift.result.FunnelResultSet;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.source.SourceKey;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * Create by lifan on 2019-07-04 12:31
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({FunnelQueryBuilder.class, QueryInfoParser.class, SwiftContext.class, FunnelSegmentQuery.class})
public class FunnelQueryBuilderTest {

    @Test
    public void buildQuery() throws Exception {

        //mock FunnelQueryInfo
        FunnelQueryBean bean = mock(FunnelQueryBean.class, Mockito.RETURNS_DEEP_STUBS);
        FunnelQueryInfo info = mock(FunnelQueryInfo.class);
        mockStatic(QueryInfoParser.class);
        when(QueryInfoParser.parse(bean)).thenReturn(info);

        //mock SwiftContext
        mockStatic(SwiftContext.class);
        SwiftContext swiftContext = mock(SwiftContext.class);
        when(SwiftContext.get()).thenReturn(swiftContext);

        //mock SwiftSegmentManager
        SwiftSegmentManager localSegmentProvider = mock(SwiftSegmentManager.class);
        when(swiftContext.getBean("localSegmentProvider", SwiftSegmentManager.class)).thenReturn(localSegmentProvider);

        //mock tableName
        String tableName = "tableName";
        when(bean.getTableName()).thenReturn(tableName);

        //mock new SourceKey
        SourceKey sourceKey = mock(SourceKey.class);
        whenNew(SourceKey.class).withArguments(tableName).thenReturn(sourceKey);

        //mock List<Segment> segments
        List<Segment> segments = new ArrayList<Segment>();
        Segment segment = mock(Segment.class);
        segments.add(segment);
        when(localSegmentProvider.getSegment(sourceKey)).thenReturn(segments);

        //init queries
        List<Query<QueryResultSet<FunnelResultSet>>> queries = new ArrayList<Query<QueryResultSet<FunnelResultSet>>>();

        //mock info.getQueryBean
        FunnelQueryBean funnelQueryBean = mock(FunnelQueryBean.class);
        when(info.getQueryBean()).thenReturn(funnelQueryBean);

        //mock new FunnelSegmentQuery
        FunnelSegmentQuery funnelSegmentQuery = mock(FunnelSegmentQuery.class);
        whenNew(FunnelSegmentQuery.class).withArguments(segment, funnelQueryBean).thenReturn(funnelSegmentQuery);

        //add
        queries.add(funnelSegmentQuery);

        //mock size
        int size = 1;
        when(bean.getAggregation().getFunnelEvents().size()).thenReturn(size);

        //mock FunnelResultQuery
        FunnelResultQuery funnelResultQuery = mock(FunnelResultQuery.class);
        whenNew(FunnelResultQuery.class).withArguments(size, queries).thenReturn(funnelResultQuery);

        //assert
        Assert.assertEquals(funnelResultQuery, FunnelQueryBuilder.buildQuery(bean));


    }

}