package com.fr.swift.query.info.group;

import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.info.element.metric.Metric;
import com.fr.swift.source.SourceKey;

/**
 * Created by pony on 2017/12/15.
 * 一张表的指标都合并在这
 */
public class TableGroupQueryInfo {
    //分组表的维度
    private Dimension[] dimensions;
    //分组表的聚合维度
    private Metric[] metrics;

    private SourceKey table;

    public TableGroupQueryInfo(Dimension[] dimensions, Metric[] metrics, SourceKey table) {
        this.dimensions = dimensions;
        this.metrics = metrics;
        this.table = table;
    }

    public Dimension[] getDimensions() {
        return dimensions;
    }

    public Metric[] getMetrics() {
        return metrics;
    }

    public SourceKey getTable() {
        return table;
    }

}
