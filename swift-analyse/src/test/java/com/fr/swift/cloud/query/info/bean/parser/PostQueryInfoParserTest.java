package com.fr.swift.cloud.query.info.bean.parser;

import com.fr.swift.cloud.query.aggregator.AggregatorType;
import com.fr.swift.cloud.query.filter.SwiftDetailFilterType;
import com.fr.swift.cloud.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.cloud.query.filter.match.DetailBasedMatchFilter;
import com.fr.swift.cloud.query.group.Groups;
import com.fr.swift.cloud.query.group.impl.NoGroupRule;
import com.fr.swift.cloud.query.group.info.IndexInfoImpl;
import com.fr.swift.cloud.query.info.bean.element.AggregationBean;
import com.fr.swift.cloud.query.info.bean.element.MetricBean;
import com.fr.swift.cloud.query.info.bean.element.SortBean;
import com.fr.swift.cloud.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.cloud.query.info.bean.element.filter.impl.InFilterBean;
import com.fr.swift.cloud.query.info.bean.post.HavingFilterQueryInfoBean;
import com.fr.swift.cloud.query.info.bean.post.PostQueryInfoBean;
import com.fr.swift.cloud.query.info.bean.post.RowSortQueryInfoBean;
import com.fr.swift.cloud.query.info.bean.query.GroupQueryInfoBean;
import com.fr.swift.cloud.query.info.bean.type.PostQueryType;
import com.fr.swift.cloud.query.info.element.dimension.Dimension;
import com.fr.swift.cloud.query.info.element.dimension.GroupDimension;
import com.fr.swift.cloud.query.info.group.post.HavingFilterQueryInfo;
import com.fr.swift.cloud.query.info.group.post.PostQueryInfo;
import com.fr.swift.cloud.query.info.group.post.RowSortQueryInfo;
import com.fr.swift.cloud.query.sort.AscSort;
import com.fr.swift.cloud.query.sort.Sort;
import com.fr.swift.cloud.query.sort.SortType;
import com.fr.swift.cloud.segment.column.ColumnKey;
import com.fr.swift.cloud.source.SourceKey;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Created by lyon on 2019/1/11.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({FilterInfoParser.class})
public class PostQueryInfoParserTest {

    @Test
    public void testRowSort() {
        RowSortQueryInfoBean bean = new RowSortQueryInfoBean();
        SortBean sortBean = new SortBean();
        sortBean.setType(SortType.DESC);
        sortBean.setName("a");
        bean.setSortBeans(Collections.singletonList(sortBean));
        Dimension dimension = new GroupDimension(0, new ColumnKey("dim"), Groups.newGroup(new NoGroupRule()),
                new AscSort(0), new IndexInfoImpl(true, false));
        MetricBean metricBean = new MetricBean();
        metricBean.setColumn("a");
        metricBean.setType(AggregatorType.SUM);
        GroupQueryInfoBean mock = Mockito.mock(GroupQueryInfoBean.class);
        Mockito.when(mock.getAggregations()).thenReturn(Collections.<AggregationBean>emptyList());
        Mockito.when(mock.getPostAggregations()).thenReturn(Collections.<PostQueryInfoBean>emptyList());
        List<PostQueryInfo> infoList = PostQueryInfoParser.parse(null, mock,
                Collections.singletonList(dimension));
        assertEquals(1, infoList.size());
        Assert.assertEquals(PostQueryType.ROW_SORT, infoList.get(0).getType());
        RowSortQueryInfo info = (RowSortQueryInfo) infoList.get(0);
        assertEquals(1, info.getSortList().size());
        Sort sort = info.getSortList().get(0);
        assertEquals(SortType.DESC, sort.getSortType());
        assertEquals(1, sort.getTargetIndex());
    }

    @Test
    public void testHavingFilter() {
        HavingFilterQueryInfoBean bean = new HavingFilterQueryInfoBean();
        bean.setColumn("a");
        FilterInfoBean filterInfoBean = new InFilterBean();
        filterInfoBean.setFilterValue(Collections.singleton("1"));
        bean.setFilter(filterInfoBean);
        Dimension dimension = new GroupDimension(0, new ColumnKey("dim"), Groups.newGroup(new NoGroupRule()),
                new AscSort(0), new IndexInfoImpl(true, false));
        MetricBean metricBean = new MetricBean();
        metricBean.setColumn("a");
        metricBean.setType(AggregatorType.SUM);

        PowerMock.mockStatic(FilterInfoParser.class);
        EasyMock.expect(FilterInfoParser.parse(EasyMock.anyObject(SourceKey.class), EasyMock.anyObject(FilterInfoBean.class)))
                .andReturn(new SwiftDetailFilterInfo<Set<Double>>(new ColumnKey("a"), Collections.singleton((double) 1),
                        SwiftDetailFilterType.IN))
                .anyTimes();
        PowerMock.replay(FilterInfoParser.class);
        GroupQueryInfoBean mock = Mockito.mock(GroupQueryInfoBean.class);
        Mockito.when(mock.getAggregations()).thenReturn(Collections.<AggregationBean>emptyList());
        Mockito.when(mock.getPostAggregations()).thenReturn(Collections.<PostQueryInfoBean>emptyList());
        List<PostQueryInfo> infoList = PostQueryInfoParser.parse(null, mock,
                Collections.singletonList(dimension));
        assertEquals(1, infoList.size());
        Assert.assertEquals(PostQueryType.HAVING_FILTER, infoList.get(0).getType());
        HavingFilterQueryInfo info = (HavingFilterQueryInfo) infoList.get(0);
        assertEquals(1, info.getMatchFilterList().size());
        DetailBasedMatchFilter filter = (DetailBasedMatchFilter) info.getMatchFilterList().get(0);
    }
}