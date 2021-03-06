package com.fr.swift.cloud.query.info.element.metric;

import com.fr.swift.cloud.query.aggregator.Aggregator;
import com.fr.swift.cloud.query.aggregator.AggregatorFactory;
import com.fr.swift.cloud.query.aggregator.AggregatorType;
import com.fr.swift.cloud.query.filter.FilterBuilder;
import com.fr.swift.cloud.query.filter.info.FilterInfo;
import com.fr.swift.cloud.query.filter.match.MatchFilter;
import com.fr.swift.cloud.query.info.bean.parser.FilterInfoParser;
import com.fr.swift.cloud.query.info.bean.type.MetricType;
import com.fr.swift.cloud.query.info.funnel.FunnelAggregationBean;
import com.fr.swift.cloud.segment.column.ColumnKey;
import com.fr.swift.cloud.source.SourceKey;

import java.util.Set;

/**
 * @author yee
 * @date 2019-07-10
 */
public class FunnelMetric extends FunnelPathsMetric {

    public FunnelMetric(int index, ColumnKey columnKey, FunnelAggregationBean bean, FilterInfo filterInfo, Set<ColumnKey> columnKeys) {
        super(index, columnKey, bean, filterInfo, columnKeys);
    }

    @Override
    public Aggregator getAggregator() {
        return AggregatorFactory.createAggregator(AggregatorType.FUNNEL, bean);
    }

    @Override
    public MetricType getMetricType() {
        return MetricType.FUNNEL;
    }

    public MatchFilter getTimeGroupFilter(SourceKey sourceKey) {
        FunnelAggregationBean funnelBean = (FunnelAggregationBean) bean;
        FilterInfo parse = FilterInfoParser.parse(sourceKey, funnelBean.getTimeGroup().filter());
        return FilterBuilder.buildMatchFilter(parse);
    }

    public boolean isPostGroup() {
        return ((FunnelAggregationBean) bean).getPostGroup() != null;
    }
}
