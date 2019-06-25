package com.fr.swift.cloud.analysis;

import com.fr.swift.cloud.result.table.TemplateUsageInfo;
import com.fr.swift.cloud.util.RelationQueryUtils;
import com.fr.swift.query.QueryRunnerProvider;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.info.bean.element.DimensionBean;
import com.fr.swift.query.info.bean.element.MetricBean;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.AndFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.InFilterBean;
import com.fr.swift.query.info.bean.query.DetailQueryInfoBean;
import com.fr.swift.query.info.bean.query.GroupQueryInfoBean;
import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.query.info.bean.query.SingleInfoBean;
import com.fr.swift.query.info.bean.type.DimensionType;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.Row;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class created on 2019/6/12
 *
 * @author Lucifer
 * @description
 */
public class TemplateUsageInfoQuery {

    public TemplateUsageInfo query(String appId, String yearMonth) throws Exception {

        FilterInfoBean filter = new AndFilterBean(
                Arrays.asList(
                        new InFilterBean("appId", appId),
                        new InFilterBean("yearMonth", yearMonth)
                ));
        GroupQueryInfoBean tNumBean = GroupQueryInfoBean.builder("template_info").setDimensions(new DimensionBean(DimensionType.GROUP, "appId")).setAggregations(new MetricBean("id", AggregatorType.DISTINCT)).setFilter(filter).build();

        GroupQueryInfoBean tvNumBean = GroupQueryInfoBean.builder("execution").setDimensions(new DimensionBean(DimensionType.GROUP, "appId"))
                .setAggregations(new MetricBean("id", AggregatorType.DISTINCT), new MetricBean("userId", AggregatorType.DISTINCT), new MetricBean("reportId", AggregatorType.DISTINCT))
                .setFilter(filter).build();


        FilterInfoBean integrationFilter = new AndFilterBean(
                Arrays.asList(
                        filter,
                        new InFilterBean("source", "integration")
                ));
        GroupQueryInfoBean linkVisitBean = GroupQueryInfoBean.builder("execution").setDimensions(new DimensionBean(DimensionType.GROUP, "appId")).setAggregations(new MetricBean("source", AggregatorType.COUNT)).setFilter(integrationFilter).build();
        //appId,861.0

        FilterInfoBean singleFilter = new AndFilterBean(
                Arrays.asList(
                        filter,
                        new InFilterBean("source", "single")
                ));
        GroupQueryInfoBean platformVisitBean = GroupQueryInfoBean.builder("execution").setDimensions(new DimensionBean(DimensionType.GROUP, "appId")).setAggregations(new MetricBean("source", AggregatorType.COUNT)).setFilter(singleFilter).build();

        //联表 appId,tNum  appId,tvNum,MAU,sumVisit  appId,linkVisit  appId,platformVisit
        List<SingleInfoBean> beans = Arrays.asList(tNumBean, tvNumBean, linkVisitBean, platformVisitBean);
        String[] relation = {"appId", "appId"};
        List<String[]> relationList = new ArrayList<>();
        for (int i = 0; i < beans.size() - 1; i++) {
            relationList.add(relation);
        }
        SwiftResultSet resultSet = RelationQueryUtils.relationAllTablesDFS(relationList, beans);
        Row row = null;
        while (resultSet.hasNext()) {
            row = resultSet.getNextRow();
        }

        DetailQueryInfoBean visitDayBean = DetailQueryInfoBean.builder("execution")
                .setDimensions(new DimensionBean(DimensionType.DETAIL, "time"))
                .setFilter(filter).build();
        SwiftResultSet visitDayResult = QueryRunnerProvider.getInstance().query(QueryBeanFactory.queryBean2String(visitDayBean));
        List<Long> timeList = new ArrayList<>();
        while (visitDayResult.hasNext()) {
            timeList.add(visitDayResult.getNextRow().getValue(0));
        }
        int visitDay = calcDays(timeList);

        return new TemplateUsageInfo(row, visitDay, yearMonth, appId);
    }

    private int calcDays(List<Long> timeList) {
        Set<Integer> daySet = new HashSet<>();
        for (Long time : timeList) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(time));
            int days = calendar.get(Calendar.DAY_OF_YEAR);
            daySet.add(days);
        }
        return daySet.size();
    }
}
