package com.fr.swift.query.segment.group;

import com.fr.swift.query.group.by2.node.GroupPage;
import com.fr.swift.query.group.by2.node.NodeGroupByUtils;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.group.info.MetricInfo;
import com.fr.swift.result.BaseNodeMergeQueryResultSet;
import com.fr.swift.result.qrs.QueryResultSet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Iterator;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * Create by lifan on 2019-07-02 09:53
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({GroupSegmentQuery.class, NodeGroupByUtils.class})
public class GroupSegmentQueryTest {
    // TODO: 2019-07-03  

    @Test
    public void getQueryResult() throws Exception {
        GroupByInfo groupByInfo1 = mock(GroupByInfo.class);
        int fetchSize = mock(Integer.class);
        when(groupByInfo1.getFetchSize()).thenReturn(fetchSize);

        MetricInfo metricInfo = mock(MetricInfo.class);

        mockStatic(NodeGroupByUtils.class);
        Iterator<QueryResultSet<GroupPage>> iterator = mock(Iterator.class, Mockito.RETURNS_DEEP_STUBS);
        when(NodeGroupByUtils.groupBy(groupByInfo1, metricInfo)).thenReturn(iterator);

        QueryResultSet resultSet = mock(QueryResultSet.class);
        when(iterator.next()).thenReturn(resultSet);

//        GroupPage groupPage = mock(GroupPage.class);
//        when(resultSet.getPage()).thenReturn(groupPage);

        GroupPage groupPage = mock(GroupPage.class);
        when(iterator.next().getPage()).thenReturn(groupPage);
        when(iterator.hasNext()).thenReturn(true);

        BaseNodeMergeQueryResultSet baseNodeMergeQueryResultSet = mock(BaseNodeMergeQueryResultSet.class);
        whenNew(BaseNodeMergeQueryResultSet.class).withArguments(groupByInfo1.getFetchSize()).thenReturn(baseNodeMergeQueryResultSet);

        Assert.assertEquals(baseNodeMergeQueryResultSet, new GroupSegmentQuery(groupByInfo1, metricInfo, true).getQueryResult());

    }

}