package com.fr.swift.cloud.query.info.element.metric;

import com.fr.swift.cloud.query.aggregator.Aggregator;
import com.fr.swift.cloud.query.aggregator.AggregatorFactory;
import com.fr.swift.cloud.query.aggregator.AggregatorType;
import com.fr.swift.cloud.query.filter.FilterBuilder;
import com.fr.swift.cloud.query.filter.detail.DetailFilter;
import com.fr.swift.cloud.query.filter.detail.impl.AllShowDetailFilter;
import com.fr.swift.cloud.query.filter.info.FilterInfo;
import com.fr.swift.cloud.query.info.bean.parser.FilterInfoParser;
import com.fr.swift.cloud.query.info.bean.type.MetricType;
import com.fr.swift.cloud.query.info.funnel.FunnelPathsAggregationBean;
import com.fr.swift.cloud.query.info.funnel.FunnelVirtualStep;
import com.fr.swift.cloud.segment.Segment;
import com.fr.swift.cloud.segment.column.ColumnKey;
import com.fr.swift.cloud.source.SourceKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author yee
 * @date 2019-07-10
 */
public class FunnelPathsMetric extends AbstractMetric {
    protected FunnelPathsAggregationBean bean;
    private Set<ColumnKey> columnKeys;

    public FunnelPathsMetric(int index, ColumnKey columnKey, FunnelPathsAggregationBean bean, FilterInfo filterInfo, Set<ColumnKey> columnKeys) {
        super(index, columnKey, filterInfo);
        this.columnKeys = columnKeys;
        this.bean = bean;
    }

    @Override
    public Aggregator getAggregator() {
        return AggregatorFactory.createAggregator(AggregatorType.FUNNEL_PATHS, bean);
    }

    @Override
    public MetricType getMetricType() {
        return MetricType.FUNNEL_PATHS;
    }

    public Set<ColumnKey> getColumnKeys() {
        return columnKeys;
    }

    public List<DetailFilter> getEventFilter(Segment segment) {
        List<DetailFilter> result = new ArrayList<DetailFilter>();
        for (FunnelVirtualStep event : bean.getSteps()) {
            if (null != event.getFilter()) {
                result.add(FilterBuilder.buildDetailFilter(segment,
                        FilterInfoParser.parse(new SourceKey(segment.getMetaData().getTableName()), event.getFilter())));
            } else {
                result.add(new AllShowDetailFilter(segment));
            }
        }
        return result;
    }
}
