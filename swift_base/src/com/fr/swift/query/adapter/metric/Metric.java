package com.fr.swift.query.adapter.metric;

import com.fr.swift.query.adapter.SwiftColumnProvider;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.filter.info.FilterInfo;

/**
 * Created by pony on 2017/12/11.
 * 度量，聚合的列
 */
public interface Metric extends SwiftColumnProvider {
    FilterInfo getFilter();

    Aggregator getAggregator();
}
