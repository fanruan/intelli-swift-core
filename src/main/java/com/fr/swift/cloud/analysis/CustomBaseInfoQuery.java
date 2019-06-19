package com.fr.swift.cloud.analysis;

import com.fr.swift.cloud.result.table.CustomerBaseInfo;
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
import java.util.List;

/**
 * This class created on 2019/6/12
 *
 * @author Lucifer
 * @description
 */
public class CustomBaseInfoQuery {

    public CustomerBaseInfo query(String appId, String yearMonth) throws Exception {
        FilterInfoBean filter = new AndFilterBean(
                Arrays.<FilterInfoBean>asList(
                        new InFilterBean("appId", appId),
                        new InFilterBean("yearMonth", yearMonth)
                ));

        GroupQueryInfoBean maxTimeBean = GroupQueryInfoBean.builder("container_message")
                .setAggregations(new MetricBean("time", AggregatorType.MAX))
                .setFilter(filter).build();
        SwiftResultSet maxTimeResultSet = QueryRunnerProvider.getInstance().query(QueryBeanFactory.queryBean2String(maxTimeBean));
        double time = 0;
        while (maxTimeResultSet.hasNext()) {
            Row row = maxTimeResultSet.getNextRow();
            time = row.getValue(0) == null ? 0 : row.getValue(0);
        }
        FilterInfoBean timeFilter = new InFilterBean("time", (long) time);
        FilterInfoBean andFilter = new AndFilterBean(
                Arrays.<FilterInfoBean>asList(filter, timeFilter)
        );
        DetailQueryInfoBean containerMessageBean = DetailQueryInfoBean.builder("container_message")
                .setDimensions(new DimensionBean(DimensionType.DETAIL_ALL_COLUMN)).setFilter(andFilter).build();
        List<Row> rowList = new ArrayList<>();
        SwiftResultSet containerMessageResult = QueryRunnerProvider.getInstance().query(QueryBeanFactory.queryBean2String(containerMessageBean));
        while (containerMessageResult.hasNext()) {
            rowList.add(containerMessageResult.getNextRow());
        }
        DetailQueryInfoBean functionPossessBean = DetailQueryInfoBean.builder("function_possess")
                .setDimensions(new DimensionBean(DimensionType.DETAIL_ALL_COLUMN)).setFilter(filter).build();
        List<Row> functionPossessRowList = new ArrayList<>();
        SwiftResultSet functionPossessResultSet = QueryRunnerProvider.getInstance().query(QueryBeanFactory.queryBean2String(functionPossessBean));
        while (functionPossessResultSet.hasNext()) {
            functionPossessRowList.add(functionPossessResultSet.getNextRow());
        }
        CustomerBaseInfo customerBaseInfo = new CustomerBaseInfo(rowList, functionPossessRowList, appId, yearMonth);
        return customerBaseInfo;
    }
}