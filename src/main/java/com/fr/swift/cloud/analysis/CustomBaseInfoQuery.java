package com.fr.swift.cloud.analysis;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.cloud.result.table.CustomerBaseInfo;
import com.fr.swift.log.SwiftLoggers;
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
import com.fr.swift.util.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2019/6/12
 *
 * @author Lucifer
 * @description
 */
@SwiftBean
@CloudQuery(name = "customBaseInfoQuery", tables = {"customer_base_info"})
public class CustomBaseInfoQuery extends AbstractSaveQueryResult implements ICloudQuery {

    public void queryAndSave(String appId, String yearMonth) throws Exception {

        SwiftLoggers.getLogger().info("start CustomBaseInfoQuery analysis task with appId: {}, yearMonth: {}", appId, yearMonth);

        FilterInfoBean filter = new AndFilterBean(
                Arrays.asList(
                        new InFilterBean("appId", appId),
                        new InFilterBean("yearMonth", yearMonth)
                ));
        //在container_message表中查找最新的time  maxTimeBean
        GroupQueryInfoBean maxTimeBean = GroupQueryInfoBean.builder("container_message")
                .setDimensions(new DimensionBean(DimensionType.GROUP, "node"))
                .setAggregations(new MetricBean("time", AggregatorType.MAX))
                .setFilter(filter).build();
        SwiftResultSet maxTimeResultSet = QueryRunnerProvider.getInstance().query(QueryBeanFactory.queryBean2String(maxTimeBean));
        Map<String, List<Row>> nodeContainerMessageRows = new HashMap<>();
        while (maxTimeResultSet.hasNext()) {
            Row row = maxTimeResultSet.getNextRow();
            double time = row.getValue(1) == null ? 0 : row.getValue(1);
            //在container_message表中查询最新时间的所有字段明细 containerMessageBean
            FilterInfoBean timeFilter = new InFilterBean("time", (long) time);
            FilterInfoBean andFilter = new AndFilterBean(
                    Arrays.asList(filter, timeFilter)
            );
            DetailQueryInfoBean containerMessageBean = DetailQueryInfoBean.builder("container_message")
                    .setDimensions(new DimensionBean(DimensionType.DETAIL_ALL_COLUMN)).setFilter(andFilter).build();
            List<Row> rowList = new ArrayList<>();
            SwiftResultSet containerMessageResult = QueryRunnerProvider.getInstance().query(QueryBeanFactory.queryBean2String(containerMessageBean));
            while (containerMessageResult.hasNext()) {
                rowList.add(containerMessageResult.getNextRow());
            }
            nodeContainerMessageRows.put(row.getValue(0), rowList);
        }
        if (nodeContainerMessageRows.isEmpty()) {
            nodeContainerMessageRows.put(Strings.EMPTY, Collections.emptyList());
        }

        // 在function_possess表中 查询所有字段明细
        DetailQueryInfoBean functionPossessBean = DetailQueryInfoBean.builder("function_possess")
                .setDimensions(new DimensionBean(DimensionType.DETAIL_ALL_COLUMN)).setFilter(filter).build();
        List<Row> functionPossessRowList = new ArrayList<>();
        SwiftResultSet functionPossessResultSet = QueryRunnerProvider.getInstance().query(QueryBeanFactory.queryBean2String(functionPossessBean));
        while (functionPossessResultSet.hasNext()) {
            functionPossessRowList.add(functionPossessResultSet.getNextRow());
        }

        List<CustomerBaseInfo> customerBaseInfoList = new ArrayList<>();
        for (Map.Entry<String, List<Row>> containerMessageEntry : nodeContainerMessageRows.entrySet()) {
            customerBaseInfoList.add(new CustomerBaseInfo(containerMessageEntry.getValue(), functionPossessRowList, appId, yearMonth));
        }
        super.saveResult(customerBaseInfoList);

        SwiftLoggers.getLogger().info("finished CustomBaseInfoQuery analysis task with appId: {}, yearMonth: {}", appId, yearMonth);
    }
}