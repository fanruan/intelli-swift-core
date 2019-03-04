package com.fr.swift.cloud.analysis;

import com.fr.swift.cloud.source.table.Execution;
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
import java.util.UUID;

/**
 * Created by lyon on 2018/12/21.
 */
public class GlobalTplMetricQuery implements MetricQuery {

    private static String[] metrics = new String[]{
            Execution.consume.getName(),
            Execution.sqlTime.getName(),
            Execution.memory.getName()
    };

    private static List<MetricBean> createMetrics() {
        List<MetricBean> metricBeans = new ArrayList<MetricBean>();
        for (String name : metrics) {
            MetricBean metricBean = new MetricBean();
            metricBean.setColumn(name);
            metricBean.setType(AggregatorType.AVERAGE);
            metricBeans.add(metricBean);
        }
        MetricBean count = new MetricBean();
        count.setColumn(Execution.id.getName());
        count.setType(AggregatorType.COUNT);
        metricBeans.add(count);
        return metricBeans;
    }

    private GroupQueryInfoBean bean;

    public GlobalTplMetricQuery(FilterInfoBean filter) {
        this.bean = new GroupQueryInfoBean();
        bean.setQueryId(UUID.randomUUID().toString());
        bean.setTableName(Execution.tableName);
        bean.setFilter(filter);
        DimensionBean dimensionBean = new DimensionBean();
        dimensionBean.setType(DimensionType.GROUP);
        dimensionBean.setColumn(Execution.tName.getName());
        bean.setDimensions(Collections.singletonList(dimensionBean));
        bean.setAggregations(createMetrics());
    }

    @Override
    public SwiftResultSet getResult() throws Exception {
        return QueryRunnerProvider.getInstance().query(QueryBeanFactory.queryBean2String(bean));
    }
}
