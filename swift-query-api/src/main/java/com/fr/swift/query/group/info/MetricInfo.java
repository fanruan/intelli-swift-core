package com.fr.swift.query.group.info;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.segment.column.Column;

import java.util.List;

/**
 * Created by Lyon on 2018/4/25.
 */
public interface MetricInfo {

    /**
     * 用于聚合的明细列
     */
    List<Column> getMetrics();

    /**
     * 聚合器。另外，指标的明细过滤也在解析的时候加到Aggregator里面去了
     */
    List<Aggregator> getAggregators();

    /**
     * 聚合结果数组的长度
     */
    int getTargetLength();
}
