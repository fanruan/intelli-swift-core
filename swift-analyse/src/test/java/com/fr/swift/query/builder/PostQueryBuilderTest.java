package com.fr.swift.query.builder;

import com.fr.swift.query.funnel.TimeWindowBean;
import com.fr.swift.query.info.bean.type.PostQueryType;
import com.fr.swift.query.info.element.target.GroupTarget;
import com.fr.swift.query.info.group.post.CalculatedFieldQueryInfo;
import com.fr.swift.query.info.group.post.FunnelPostQueryInfo;
import com.fr.swift.query.info.group.post.HavingFilterQueryInfo;
import com.fr.swift.query.info.group.post.PostQueryInfo;
import com.fr.swift.query.info.group.post.RowSortQueryInfo;
import com.fr.swift.query.info.group.post.TreeAggregationQueryInfo;
import com.fr.swift.query.info.group.post.TreeFilterQueryInfo;
import com.fr.swift.query.info.group.post.TreeSortQueryInfo;
import com.fr.swift.query.post.FieldCalQuery;
import com.fr.swift.query.post.FunnelTimeMedianPostQuery;
import com.fr.swift.query.post.HavingFilterQuery;
import com.fr.swift.query.post.RowSortQuery;
import com.fr.swift.query.post.TreeAggregationQuery;
import com.fr.swift.query.post.TreeFilterQuery;
import com.fr.swift.query.post.TreeSortQuery;
import com.fr.swift.query.query.Query;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.qrs.QueryResultSet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * Create by lifan on 2019-07-04 17:12
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({PostQueryBuilder.class})
public class PostQueryBuilderTest {

    @Test
    public void buildQuery1() throws Exception {
        //mock tmpQuery
        Query<QueryResultSet<SwiftNode>> tmpQuery = mock(Query.class);
        //init postQueryInfoList
        //mock postQueryInfo
        List<PostQueryInfo> postQueryInfoList = new ArrayList<PostQueryInfo>();
        CalculatedFieldQueryInfo postQueryInfo = mock(CalculatedFieldQueryInfo.class);
        PostQueryType type = PostQueryType.CAL_FIELD;
        when(postQueryInfo.getType()).thenReturn(type);
        postQueryInfoList.add(postQueryInfo);
        GroupTarget calInfo = mock(GroupTarget.class);
        when(postQueryInfo.getCalInfo()).thenReturn(calInfo);
        FieldCalQuery fieldCalQuery = mock(FieldCalQuery.class);
        whenNew(FieldCalQuery.class).withArguments(tmpQuery, calInfo).thenReturn(fieldCalQuery);
        Assert.assertEquals(fieldCalQuery, PostQueryBuilder.buildQuery(tmpQuery, postQueryInfoList));
    }

    @Test
    public void buildQuery2() throws Exception {
        //mock tmpQuery
        Query<QueryResultSet<SwiftNode>> tmpQuery = mock(Query.class);
        //init postQueryInfoList
        //mock postQueryInfo
        List<PostQueryInfo> postQueryInfoList = new ArrayList<PostQueryInfo>();
        HavingFilterQueryInfo postQueryInfo = mock(HavingFilterQueryInfo.class);
        PostQueryType type = PostQueryType.HAVING_FILTER;
        when(postQueryInfo.getType()).thenReturn(type);
        postQueryInfoList.add(postQueryInfo);
        List matchFilterList = mock(List.class);
        when(postQueryInfo.getMatchFilterList()).thenReturn(matchFilterList);
        HavingFilterQuery havingFilterQuery = mock(HavingFilterQuery.class);
        whenNew(HavingFilterQuery.class).withArguments(tmpQuery, matchFilterList).thenReturn(havingFilterQuery);
        Assert.assertEquals(havingFilterQuery, PostQueryBuilder.buildQuery(tmpQuery, postQueryInfoList));
    }

    @Test
    public void buildQuery3() throws Exception {
        //mock tmpQuery
        Query<QueryResultSet<SwiftNode>> tmpQuery = mock(Query.class);
        //init postQueryInfoList
        //mock postQueryInfo
        List<PostQueryInfo> postQueryInfoList = new ArrayList<PostQueryInfo>();
        TreeFilterQueryInfo postQueryInfo = mock(TreeFilterQueryInfo.class);
        PostQueryType type = PostQueryType.TREE_FILTER;
        when(postQueryInfo.getType()).thenReturn(type);
        postQueryInfoList.add(postQueryInfo);
        List matchFilterList = mock(List.class);
        when(postQueryInfo.getMatchFilterList()).thenReturn(matchFilterList);
        TreeFilterQuery treeFilterQuery = mock(TreeFilterQuery.class);
        whenNew(TreeFilterQuery.class).withArguments(tmpQuery, matchFilterList).thenReturn(treeFilterQuery);
        Assert.assertEquals(treeFilterQuery, PostQueryBuilder.buildQuery(tmpQuery, postQueryInfoList));
    }

    @Test
    public void buildQuery4() throws Exception {
        //mock tmpQuery
        Query<QueryResultSet<SwiftNode>> tmpQuery = mock(Query.class);
        //init postQueryInfoList
        //mock postQueryInfo
        List<PostQueryInfo> postQueryInfoList = new ArrayList<PostQueryInfo>();
        TreeAggregationQueryInfo postQueryInfo = mock(TreeAggregationQueryInfo.class);
        PostQueryType type = PostQueryType.TREE_AGGREGATION;
        when(postQueryInfo.getType()).thenReturn(type);
        postQueryInfoList.add(postQueryInfo);
        List aggregators = mock(List.class);
        when(postQueryInfo.getAggregators()).thenReturn(aggregators);
        TreeAggregationQuery treeAggregationQuery = mock(TreeAggregationQuery.class);
        whenNew(TreeAggregationQuery.class).withArguments(tmpQuery, aggregators).thenReturn(treeAggregationQuery);
        Assert.assertEquals(treeAggregationQuery, PostQueryBuilder.buildQuery(tmpQuery, postQueryInfoList));
    }

    @Test
    public void buildQuery5() throws Exception {
        //mock tmpQuery
        Query<QueryResultSet<SwiftNode>> tmpQuery = mock(Query.class);
        //init postQueryInfoList
        //mock postQueryInfo
        List<PostQueryInfo> postQueryInfoList = new ArrayList<PostQueryInfo>();
        TreeSortQueryInfo postQueryInfo = mock(TreeSortQueryInfo.class);
        PostQueryType type = PostQueryType.TREE_SORT;
        when(postQueryInfo.getType()).thenReturn(type);
        postQueryInfoList.add(postQueryInfo);
        List sortList = mock(List.class);
        when(postQueryInfo.getSortList()).thenReturn(sortList);
        TreeSortQuery treeSortQuery = mock(TreeSortQuery.class);
        whenNew(TreeSortQuery.class).withArguments(tmpQuery, sortList).thenReturn(treeSortQuery);
        Assert.assertEquals(treeSortQuery, PostQueryBuilder.buildQuery(tmpQuery, postQueryInfoList));
    }

    @Test
    public void buildQuery6() throws Exception {
        //mock tmpQuery
        Query<QueryResultSet<SwiftNode>> tmpQuery = mock(Query.class);
        //init postQueryInfoList
        //mock postQueryInfo
        List<PostQueryInfo> postQueryInfoList = new ArrayList<PostQueryInfo>();
        RowSortQueryInfo postQueryInfo = mock(RowSortQueryInfo.class);
        PostQueryType type = PostQueryType.ROW_SORT;
        when(postQueryInfo.getType()).thenReturn(type);
        postQueryInfoList.add(postQueryInfo);
        List sortList = mock(List.class);
        when(postQueryInfo.getSortList()).thenReturn(sortList);
        RowSortQuery rowSortQuery = mock(RowSortQuery.class);
        whenNew(RowSortQuery.class).withArguments(tmpQuery, sortList).thenReturn(rowSortQuery);
        Assert.assertEquals(rowSortQuery, PostQueryBuilder.buildQuery(tmpQuery, postQueryInfoList));
    }

    @Test
    public void buildQuery7() throws Exception {
        //mock tmpQuery
        Query<QueryResultSet<SwiftNode>> tmpQuery = mock(Query.class);
        //init postQueryInfoList
        //mock postQueryInfo
        List<PostQueryInfo> postQueryInfoList = new ArrayList<PostQueryInfo>();
        FunnelPostQueryInfo postQueryInfo = mock(FunnelPostQueryInfo.class);
        PostQueryType type = PostQueryType.FUNNEL_TIME_MEDIAN;
        when(postQueryInfo.getType()).thenReturn(type);
        postQueryInfoList.add(postQueryInfo);
        TimeWindowBean funnelQueryBean = mock(TimeWindowBean.class);
        when(postQueryInfo.getTimeWindowBean()).thenReturn(funnelQueryBean);
        FunnelTimeMedianPostQuery funnelPostQuery = mock(FunnelTimeMedianPostQuery.class);
        whenNew(FunnelTimeMedianPostQuery.class).withArguments(tmpQuery, funnelQueryBean).thenReturn(funnelPostQuery);
        Assert.assertEquals(funnelPostQuery, PostQueryBuilder.buildQuery(tmpQuery, postQueryInfoList));
    }


}