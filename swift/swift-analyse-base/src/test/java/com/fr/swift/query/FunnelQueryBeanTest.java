package com.fr.swift.query;

import com.fr.swift.base.json.JsonBuilder;
import com.fr.swift.query.info.bean.element.aggregation.FunnelFunctionBean;
import com.fr.swift.query.info.bean.element.aggregation.funnel.AssociationFilterBean;
import com.fr.swift.query.info.bean.element.aggregation.funnel.DayFilterBean;
import com.fr.swift.query.info.bean.element.aggregation.funnel.ParameterColumnsBean;
import com.fr.swift.query.info.bean.element.aggregation.funnel.PostGroupBean;
import com.fr.swift.query.info.bean.post.FunnelMedianInfoBean;
import com.fr.swift.query.info.bean.post.PostQueryInfoBean;
import com.fr.swift.query.info.bean.query.FunnelQueryBean;
import com.fr.swift.query.info.bean.type.PostQueryType;
import com.fr.swift.query.query.QueryType;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public class FunnelQueryBeanTest extends TestCase {

    public void testBean() throws Exception {
        String tableName = "yiguan";
        int timeWindow = 24 * 60 * 60;
        List<String> funnelEvents = Arrays.asList("a", "b", "c");
        String associatedColumn = "a";
        List<Integer> associatedIndexes = Arrays.asList(1);

        FunnelQueryBean bean = new FunnelQueryBean();
        bean.setTableName(tableName);
        FunnelFunctionBean functionBean = new FunnelFunctionBean();
        functionBean.setTimeWindow(timeWindow);
        functionBean.setFunnelEvents(funnelEvents);
        AssociationFilterBean associationFilterBean = new AssociationFilterBean();
        associationFilterBean.setColumn(associatedColumn);
        associationFilterBean.setFunnelIndexes(associatedIndexes);
        functionBean.setAssociatedFilter(associationFilterBean);
        ParameterColumnsBean parameterColumnsBean = new ParameterColumnsBean();
        parameterColumnsBean.setCombine("combine");
        parameterColumnsBean.setEvent("event_type");
        parameterColumnsBean.setUserId("userId");
        functionBean.setColumns(parameterColumnsBean);
        DayFilterBean dayFilterBean = new DayFilterBean();
        dayFilterBean.setDayStart("20180601");
        dayFilterBean.setNumberOfDays(10);
        functionBean.setDayFilter(dayFilterBean);
        PostGroupBean postGroupBean = new PostGroupBean();
        postGroupBean.setColumn("good_name");
        postGroupBean.setFunnelIndex(1);
        functionBean.setPostGroup(postGroupBean);
        bean.setAggregation(functionBean);
        PostQueryInfoBean postQueryInfoBean = new FunnelMedianInfoBean();
        bean.setPostAggregations(Arrays.asList(postQueryInfoBean));
        String queryString = JsonBuilder.writeJsonString(bean);
        System.out.println(queryString);
        bean = JsonBuilder.readValue(queryString, FunnelQueryBean.class);
        assertNotNull(bean);
        assertEquals(tableName, bean.getTableName());
        assertEquals(QueryType.FUNNEL, bean.getQueryType());
        functionBean = bean.getAggregation();
        assertEquals(funnelEvents, functionBean.getFunnelEvents());
        assertEquals(associatedColumn, functionBean.getAssociatedFilter().getColumn());
        assertEquals(associatedIndexes, functionBean.getAssociatedFilter().getFunnelIndexes());

        parameterColumnsBean = bean.getAggregation().getColumns();
        assertEquals("combine", parameterColumnsBean.getCombine());
        assertEquals("event_type", parameterColumnsBean.getEvent());
        assertEquals("userId", parameterColumnsBean.getUserId());

        postGroupBean = bean.getAggregation().getPostGroup();
        assertEquals("good_name", postGroupBean.getColumn());
        assertEquals(1, postGroupBean.getFunnelIndex());

        assertTrue(bean.getPostAggregations().size() == 1);
        assertEquals(PostQueryType.FUNNEL_MEDIAN, bean.getPostAggregations().get(0).getType());

        bean = JsonBuilder.readValue(funnelQuery, FunnelQueryBean.class);
        assertNotNull(bean);
    }

    private static String funnelQuery = "{\n" +
            "  \"fetchSize\": 200,\n" +
            "  \"aggregation\": {\n" +
            "    \"timeWindow\": 86400,\n" +
            "    \"columns\": {\n" +
            "      \"date\": \"date\",\n" +
            "      \"event\": \"eventName\",\n" +
            "      \"userId\": \"id\",\n" +
            "      \"combine\": \"combine\"\n" +
            "    },\n" +
            "    \"funnelEvents\": [\n" +
            "      \"login\",\n" +
            "      \"browseGoods\",\n" +
            "      \"addCart\"\n" +
            "    ],\n" +
            "    \"dayFilter\": {\n" +
            "      \"column\": \"date\",\n" +
            "      \"dayStart\": \"20180601\",\n" +
            "      \"numberOfDays\": 10\n" +
            "    }\n" +
            "  },\n" +
            "  \"postAggregations\": [],\n" +
            "  \"sorts\": [],\n" +
            "  \"tableName\": \"test_yiguan\",\n" +
            "  \"dimensions\": [],\n" +
            "  \"queryType\": \"FUNNEL\"\n" +
            "}";
}
