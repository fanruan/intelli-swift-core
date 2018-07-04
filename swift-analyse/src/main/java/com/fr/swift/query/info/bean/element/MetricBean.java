package com.fr.swift.query.info.bean.element;

import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.relation.IRelationSourceBean;
import com.fr.swift.query.info.element.metric.Metric;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Lyon on 2018/6/2.
 */
public class MetricBean {

    @JsonProperty
    private AggregatorType type;
    @JsonProperty
    private String column;
    @JsonProperty
    private IRelationSourceBean relation;
    @JsonProperty
    private FilterInfoBean filterInfoBean;
    @JsonProperty
    private String formula;
    @JsonProperty
    private Metric.MetricType metricType;

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public IRelationSourceBean getRelation() {
        return relation;
    }

    public void setRelation(IRelationSourceBean relation) {
        this.relation = relation;
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
