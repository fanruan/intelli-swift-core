package com.fr.swift.cloud.analysis;

import com.fr.swift.cloud.result.table.TemplateUsageInfo;
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
                Arrays.<FilterInfoBean>asList(
                        new InFilterBean("appId", appId),
                        new InFilterBean("yearMonth", yearMonth)
                ));
        GroupQueryInfoBean tNumBean = GroupQueryInfoBean.builder("template_info").setAggregations(new MetricBean("id", AggregatorType.DISTINCT)).setFilter(filter).build();
        SwiftResultSet tNumResultSet = QueryRunnerProvider.getInstance().query(QueryBeanFactory.queryBean2String(tNumBean));
        int tNum = 0;
        while (tNumResultSet.hasNext()) {
            Row row = tNumResultSet.getNextRow();
            tNum = row.getValue(0) != null ? ((Double) row.getValue(0)).intValue() : 0;
        }
        GroupQueryInfoBean tvNumBean = GroupQueryInfoBean.builder("execution")
                .setAggregations(new MetricBean("id", AggregatorType.DISTINCT), new MetricBean("userId", AggregatorType.DISTINCT))
                .setFilter(filter).build();
        SwiftResultSet tvNumResultSet = QueryRunnerProvider.getInstance().query(QueryBeanFactory.queryBean2String(tvNumBean));
        int tvNum = 0;
        int MAU = 0;
        int sumVisit = 0;
        while (tvNumResultSet.hasNext()) {
            Row row = tvNumResultSet.getNextRow();
            tvNum = row.getValue(0) != null ? ((Double) row.getValue(0)).intValue() : 0;
            MAU = row.getValue(1) != null ? ((Double) row.getValue(1)).intValue() : 0;
            sumVisit = tvNum;
        }

        FilterInfoBean integrationFilter = new AndFilterBean(
                Arrays.<FilterInfoBean>asList(
                        filter,
                        new InFilterBean("source", "integration")
                ));
        GroupQueryInfoBean linkVisitBean = GroupQueryInfoBean.builder("execution").setAggregations(new MetricBean("source", AggregatorType.COUNT)).setFilter(integrationFilter).build();
        SwiftResultSet linkVisitResultSet = QueryRunnerProvider.getInstance().query(QueryBeanFactory.queryBean2String(linkVisitBean));
        int linkVisit = 0;
        while (linkVisitResultSet.hasNext()) {
            Row row = linkVisitResultSet.getNextRow();
            linkVisit = row.getValue(0) != null ? ((Double) row.getValue(0)).intValue() : 0;
        }

        FilterInfoBean singleFilter = new AndFilterBean(
                Arrays.<FilterInfoBean>asList(
                        filter,
                        new InFilterBean("source", "single")
                ));
        GroupQueryInfoBean platformVisitBean = GroupQueryInfoBean.builder("execution").setAggregations(new MetricBean("source", AggregatorType.COUNT)).setFilter(singleFilter).build();
        SwiftResultSet platformVisitResultSet = QueryRunnerProvider.getInstance().query(QueryBeanFactory.queryBean2String(platformVisitBean));
        int platformVisit = 0;
        while (platformVisitResultSet.hasNext()) {
            Row row = platformVisitResultSet.getNextRow();
            platformVisit = row.getValue(0) != null ? ((Double) row.getValue(0)).intValue() : 0;
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

        return new TemplateUsageInfo(tNum, tvNum, MAU, linkVisit, platformVisit, sumVisit, visitDay, yearMonth, appId);
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
