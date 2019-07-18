package com.fr.swift.query.info.element.metric;

import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorFactory;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.filter.FilterBuilder;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.detail.impl.AllShowDetailFilter;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.info.bean.parser.FilterInfoParser;
import com.fr.swift.query.info.bean.type.MetricType;
import com.fr.swift.query.info.funnel.FunnelPathsAggregationBean;
import com.fr.swift.query.info.funnel.FunnelVirtualEvent;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SourceKey;

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
        for (FunnelVirtualEvent event : bean.getSteps()) {
            if (null != event.getFilter()) {
                try {
                    result.add(FilterBuilder.buildDetailFilter(segment,
                            FilterInfoParser.parse(new SourceKey(segment.getMetaData().getTableName()), event.getFilter())));
                } catch (SwiftMetaDataException e) {
                    SwiftLoggers.getLogger().warn("Funnel get event filter error. using all show", e);
                    result.add(new AllShowDetailFilter(segment));
                }
            } else {
                result.add(new AllShowDetailFilter(segment));
            }
        }
        return result;
    }
}
