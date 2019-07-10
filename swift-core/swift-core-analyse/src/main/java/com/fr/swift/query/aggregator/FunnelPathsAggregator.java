package com.fr.swift.query.aggregator;

import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.info.funnel.FunnelPathsAggregationBean;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.structure.iterator.RowTraversal;

import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2019-07-10
 */
public class FunnelPathsAggregator extends MultiColumnAggregator<FunnelAggregatorValue> {

    private FunnelPathsAggregationBean bean;
    private List<DetailFilter> eventFilters;

    public FunnelPathsAggregator(FunnelPathsAggregationBean bean) {
        this.bean = bean;
    }

    @Override
    public FunnelAggregatorValue aggregate(RowTraversal traversal, Map<ColumnKey, Column<?>> columns) {
        return null;
    }

    @Override
    public FunnelAggregatorValue createAggregatorValue(AggregatorValue<?> value) {
        return null;
    }

    @Override
    public AggregatorType getAggregatorType() {
        return AggregatorType.FUNNEL;
    }

    @Override
    public void combine(FunnelAggregatorValue current, FunnelAggregatorValue other) {

    }

    public void setEventFilters(List<DetailFilter> eventFilters) {
        this.eventFilters = eventFilters;
    }
}
