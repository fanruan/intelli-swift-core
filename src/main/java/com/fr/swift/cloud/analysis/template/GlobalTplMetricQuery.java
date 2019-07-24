package com.fr.swift.cloud.analysis.template;

import com.fr.swift.query.QueryRunnerProvider;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.info.bean.element.DimensionBean;
import com.fr.swift.query.info.bean.element.MetricBean;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.query.GroupQueryInfoBean;
import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.query.info.bean.type.DimensionType;
import com.fr.swift.result.SwiftResultSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lyon on 2018/12/21.
 */
public class GlobalTplMetricQuery implements MetricQuery {

    private static String[] metrics = new String[]{
            "consume",
            "coreConsume",
            "sqlTime",
    };

    private static List<MetricBean> createMetrics(FilterInfoBean top10) {
        List<MetricBean> metricBeans = new ArrayList<MetricBean>();
        metricBeans.add(MetricBean.builder("consume", AggregatorType.AVERAGE)
                .setFilter(top10).build());
        for (String name : metrics) {
            metricBeans.add(new MetricBean(name, AggregatorType.AVERAGE));
        }
        // 统计平均内存用于估算格子数
        metricBeans.add(new MetricBean("memory", AggregatorType.AVERAGE));
        metricBeans.add(new MetricBean("id", AggregatorType.COUNT));
        for (String name : metrics) {
            metricBeans.add(new MetricBean(name, AggregatorType.MAX));
        }
        return metricBeans;
    }

    private GroupQueryInfoBean bean;

    public GlobalTplMetricQuery(FilterInfoBean filter, FilterInfoBean tp10MetricFilter) {
        this.bean = new GroupQueryInfoBean();
        bean.setFilter(filter);
        bean.setTableName("execution");
        bean.setDimensions(Collections.singletonList(
                new DimensionBean(DimensionType.GROUP, "tName")));
        bean.setAggregations(createMetrics(tp10MetricFilter));
    }

    @Override
    public SwiftResultSet getResult() throws Exception {
        return QueryRunnerProvider.getInstance().query(QueryBeanFactory.queryBean2String(bean));
    }
}
