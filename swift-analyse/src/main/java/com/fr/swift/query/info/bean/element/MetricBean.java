package com.fr.swift.query.info.bean.element;

import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.info.element.metric.Metric;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SourceKey;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Lyon on 2018/6/2.
 */
public class MetricBean {

    @JsonProperty
    private AggregatorType type;
    @JsonProperty
    private SourceKey sourceKey;
    @JsonProperty
    private ColumnKey columnKey;
    @JsonProperty
    private FilterInfoBean filterInfoBean;
    @JsonProperty
    private String formula;
    @JsonProperty
    private Metric.MetricType metricType;

    public SourceKey getSourceKey() {
        return sourceKey;
    }

    public void setSourceKey(SourceKey sourceKey) {
        this.sourceKey = sourceKey;
    }

    public ColumnKey getColumnKey() {
        return columnKey;
    }

    public void setColumnKey(ColumnKey columnKey) {
        this.columnKey = columnKey;
    }

    public FilterInfoBean getFilterInfoBean() {
        return filterInfoBean;
    }

    public void setFilterInfoBean(FilterInfoBean filterInfoBean) {
        this.filterInfoBean = filterInfoBean;
    }

    public AggregatorType getType() {
        return type;
    }

    public void setType(AggregatorType type) {
        this.type = type;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public Metric.MetricType getMetricType() {
        return metricType;
    }

    public void setMetricType(Metric.MetricType metricType) {
        this.metricType = metricType;
    }
}
