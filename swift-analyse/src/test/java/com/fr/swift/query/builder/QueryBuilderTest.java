package com.fr.swift.query.builder;

import com.fr.swift.query.info.bean.parser.QueryInfoParser;
import com.fr.swift.query.info.bean.query.DetailQueryInfoBean;
import com.fr.swift.query.info.bean.query.GroupQueryInfoBean;
import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.query.info.bean.query.QueryInfoBean;
import com.fr.swift.query.post.UpdateNodeDataQuery;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.query.QueryType;
import com.fr.swift.result.qrs.QueryResultSet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by lifan on 2019-07-03 10:28
 */


@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor("com.fr.swift.query.builder.DetailQueryBuilder")
@PrepareForTest({QueryBeanFactory.class, DetailQueryBuilder.class, GroupQueryBuilder.class, QueryInfoParser.class, QueryBuilder.class, PostQueryBuilder.class})
public class QueryBuilderTest {

    @Test
    public void buildQuery() throws Exception {
        String queryBean = PowerMockito.mock(String.class);
        PowerMockito.mockStatic(QueryBeanFactory.class);

        QueryType detail = QueryType.DETAIL;
        QueryType group = QueryType.GROUP;

        QueryInfoBean detailQueryInfoBean = PowerMockito.mock(DetailQueryInfoBean.class);
        PowerMockito.when(QueryBeanFactory.create(queryBean)).thenReturn(detailQueryInfoBean);
        PowerMockito.when(detailQueryInfoBean.getQueryType()).thenReturn(detail);
        DetailQueryBuilder detailQueryBuilder = PowerMockito.mock(DetailQueryBuilder.class);
        PowerMockito.mockStatic(DetailQueryBuilder.class);
        PowerMockito.when(DetailQueryBuilder.of((DetailQueryInfoBean) detailQueryInfoBean)).thenReturn(detailQueryBuilder);
        Query detailQuery = PowerMockito.mock(Query.class);
        PowerMockito.when(detailQueryBuilder.buildQuery()).thenReturn(detailQuery);
        Assert.assertEquals(detailQuery, QueryBuilder.buildQuery(queryBean));

        QueryInfoBean groupQueryInfoBean = PowerMockito.mock(GroupQueryInfoBean.class);
        PowerMockito.when(QueryBeanFactory.create(queryBean)).thenReturn(groupQueryInfoBean);
        PowerMockito.when(groupQueryInfoBean.getQueryType()).thenReturn(group);
        GroupQueryBuilder groupQueryBuilder = PowerMockito.mock(GroupQueryBuilder.class);
        PowerMockito.mockStatic(GroupQueryBuilder.class);
        PowerMockito.when(GroupQueryBuilder.get()).thenReturn(groupQueryBuilder);
        Query groupQuery = PowerMockito.mock(Query.class);
        PowerMockito.when(groupQueryBuilder.buildQuery((GroupQueryInfoBean) groupQueryInfoBean)).thenReturn(groupQuery);
        Assert.assertEquals(groupQuery, QueryBuilder.buildQuery(queryBean));
    }

    @Test
    public void buildPostQuery() throws Exception {

        //mock List<PostQueryInfo> postQueryInfoList
        QueryInfoBean bean = PowerMockito.mock(QueryInfoBean.class);
        PowerMockito.mockStatic(QueryInfoParser.class);
        List postQueryInfoList = new ArrayList();
        PowerMockito.when(QueryInfoParser.parsePostQuery(bean)).thenReturn(postQueryInfoList);

        QueryType groupType = QueryType.GROUP;
        //mock bean.getQueryType()
        PowerMockito.when(bean.getQueryType()).thenReturn(groupType);

        //mock QueryResultSet
        QueryResultSet queryResultSet = PowerMockito.mock(QueryResultSet.class);

        //mock query
        UpdateNodeDataQuery updateNodeDataQuery = PowerMockito.mock(UpdateNodeDataQuery.class);
        PowerMockito.whenNew(UpdateNodeDataQuery.class).withArguments(queryResultSet).thenReturn(updateNodeDataQuery);

        //mock PostQueryBuilder.buildQuery
        Query query = PowerMockito.mock(Query.class);
        PowerMockito.mockStatic(PostQueryBuilder.class);
        PowerMockito.when(PostQueryBuilder.buildQuery(updateNodeDataQuery, postQueryInfoList)).thenReturn(query);
        Assert.assertEquals(query, QueryBuilder.buildPostQuery(queryResultSet, bean));

    }

}