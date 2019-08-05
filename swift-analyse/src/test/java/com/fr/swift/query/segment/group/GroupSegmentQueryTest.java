package com.fr.swift.query.segment.group;

import com.fr.swift.query.group.by2.node.GroupPage;
import com.fr.swift.query.group.by2.node.NodeGroupByUtils;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.group.info.MetricInfo;
import com.fr.swift.result.qrs.QueryResultSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/7/1
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({NodeGroupByUtils.class})
public class GroupSegmentQueryTest {
    @Test
    public void getQueryResult() {
        GroupByInfo groupByInfo = mock(GroupByInfo.class);
        MetricInfo metricInfo = mock(MetricInfo.class);

        mockStatic(NodeGroupByUtils.class);
        Iterator<GroupPage> pageItr = mock(Iterator.class);
        when(NodeGroupByUtils.groupBy(groupByInfo, metricInfo)).thenReturn(pageItr);

        when(pageItr.hasNext()).thenReturn(true, false);
        GroupPage page = mock(GroupPage.class);
        when(pageItr.next()).thenReturn(page);

        QueryResultSet<GroupPage> resultSet = new GroupSegmentQuery(groupByInfo, metricInfo).getQueryResult();
        assertThat(resultSet.getFetchSize()).isEqualTo(0);
        assertThat(resultSet.hasNextPage()).isEqualTo(true);
        assertThat(resultSet.hasNextPage()).isEqualTo(false);

        assertThat(resultSet.getPage()).isEqualTo(page);
    }
}




