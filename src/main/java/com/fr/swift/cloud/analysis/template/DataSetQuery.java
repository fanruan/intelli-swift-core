package com.fr.swift.cloud.analysis.template;

import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.QueryRunnerProvider;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.info.bean.element.DimensionBean;
import com.fr.swift.query.info.bean.element.MetricBean;
import com.fr.swift.query.info.bean.element.SortBean;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.AndFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.InFilterBean;
import com.fr.swift.query.info.bean.post.PostQueryInfoBean;
import com.fr.swift.query.info.bean.post.RowSortQueryInfoBean;
import com.fr.swift.query.info.bean.query.GroupQueryInfoBean;
import com.fr.swift.query.info.bean.query.QueryInfoBean;
import com.fr.swift.query.info.bean.type.DimensionType;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.Row;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lyon on 2019-04-19.
 */
public class DataSetQuery {

    private static final int TOP_N = 3;
    // 统计2秒以上的数据集
    private static final int LATENCY = 2000;

    private String customerId;
    private String yearMonth;
    private List<String> templates;

    public DataSetQuery(String customerId, String yearMonth, List<String> templates) {
        this.customerId = customerId;
        this.yearMonth = yearMonth;
        this.templates = templates;
    }

    /**
     * 求出每张模板耗时最长的数据集名称
     */
    public Map<String, String[]> topNDataSet() {
        Map<String, String[]> result = new HashMap<String, String[]>();
        for (String template : templates) {
            List<Long> timestamps = getTimeStamps(template);
            QueryInfoBean query = GroupQueryInfoBean.builder("execution_sql")
                    .setFilter(new AndFilterBean(Arrays.<FilterInfoBean>asList(
                            new InFilterBean("appId", customerId),
                            new InFilterBean("yearMonth", yearMonth),
                            new InFilterBean("time", timestamps.toArray(new Object[timestamps.size()]))
                    )))
                    .setDimensions(new DimensionBean(DimensionType.GROUP, "dsName"))
                    .setAggregations(new MetricBean("sqlTime", AggregatorType.AVERAGE))
                    .setPostAggregations(Collections.<PostQueryInfoBean>singletonList(
                            new RowSortQueryInfoBean(Collections.singletonList(new SortBean(SortType.DESC, "sqlTime")))
                    ))
                    .build();
            try {
                SwiftResultSet resultSet = QueryRunnerProvider.getInstance().query(query);
                String[] dsList = new String[TOP_N];
                Arrays.fill(dsList, "");
                int count = 0;
                while (count < TOP_N && resultSet.hasNext()) {
                    Row row = resultSet.getNextRow();
                    if (((Double) row.getValue(1)) < LATENCY) {
                        break;
                    }
                    dsList[count] = row.getValue(0);
                    count++;
                }
                result.put(template, dsList);
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
            }
        }
        return result;
    }

    private List<Long> getTimeStamps(String template) {
        FilterInfoBean filter = new AndFilterBean(Arrays.<FilterInfoBean>asList(
                new InFilterBean("appId", customerId),
                new InFilterBean("yearMonth", yearMonth),
                new InFilterBean("tName", template)
        ));
        QueryInfoBean query = GroupQueryInfoBean.builder("execution")
                .setFilter(filter)
                .setDimensions(new DimensionBean(DimensionType.GROUP, "time"))
                .build();
        List<Long> result = new ArrayList<Long>();
        try {
            SwiftResultSet resultSet = QueryRunnerProvider.getInstance().query(query);
            while (resultSet.hasNext()) {
                Row row = resultSet.getNextRow();
                if (row.getSize() == 1) {
                    result.add((Long) row.getValue(0));
                }
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
        return result;
    }

}
