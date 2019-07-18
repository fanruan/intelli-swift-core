package com.fr.swift.query.info.bean.parser;

import com.fr.swift.query.aggregator.AggregatorFactory;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.info.bean.element.AggregationBean;
import com.fr.swift.query.info.element.metric.FunnelMetric;
import com.fr.swift.query.info.element.metric.FunnelPathsMetric;
import com.fr.swift.query.info.element.metric.GroupMetric;
import com.fr.swift.query.info.element.metric.Metric;
import com.fr.swift.query.info.funnel.FunnelAggregationBean;
import com.fr.swift.query.info.funnel.FunnelAssociationBean;
import com.fr.swift.query.info.funnel.FunnelPathsAggregationBean;
import com.fr.swift.query.info.funnel.ParameterColumnsBean;
import com.fr.swift.query.info.funnel.group.post.PostGroupBean;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SourceKey;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Lyon on 2018/6/7.
 */
class MetricParser {

    static List<Metric> parse(SourceKey table, List<AggregationBean> metricBeans) {
        List<Metric> metrics = new ArrayList<Metric>();
        for (AggregationBean bean : metricBeans) {
            // TODO: 2018/6/7 过滤待适配
            FilterInfo filterInfo = FilterInfoParser.parse(table, bean.getFilter());
            ColumnKey columnKey = new ColumnKey(bean.getColumn());
            switch (bean.getType()) {
                case FUNNEL_PATHS:
                    Set<ColumnKey> funnelPathColumns = getFunnelColumns((FunnelPathsAggregationBean) bean);
                    funnelPathColumns.add(columnKey);
                    metrics.add(new FunnelPathsMetric(0, columnKey, (FunnelPathsAggregationBean) bean, filterInfo, funnelPathColumns));
                    break;
                case FUNNEL:
                    Set<ColumnKey> funnelColumns = getFunnelColumns((FunnelPathsAggregationBean) bean);
                    funnelColumns.add(columnKey);
                    metrics.add(new FunnelMetric(0, columnKey, (FunnelAggregationBean) bean, filterInfo, funnelColumns));
                    break;
                default:
                    metrics.add(new GroupMetric(0,
                            columnKey, filterInfo, AggregatorFactory.createAggregator(bean.getType(), bean.getParams())));
                    break;
            }
        }
        return metrics;
    }

    private static Set<ColumnKey> getFunnelColumns(FunnelPathsAggregationBean bean) {
        Set<ColumnKey> columnKeys = new HashSet<ColumnKey>();
        ParameterColumnsBean columns = bean.getColumns();
        columnKeys.add(new ColumnKey(columns.getTimestamp()));
        columnKeys.add(new ColumnKey(columns.getId()));

        FunnelAssociationBean association = bean.getAssociation();
        if (null != association) {
            columnKeys.add(new ColumnKey(association.getColumn()));
        }

        if (bean.getType() == AggregatorType.FUNNEL) {
            FunnelAggregationBean funnel = (FunnelAggregationBean) bean;
            PostGroupBean postGroup = funnel.getPostGroup();
            if (null != postGroup) {
                columnKeys.add(new ColumnKey(postGroup.getColumn()));
            }
        }
        return columnKeys;
    }
}
