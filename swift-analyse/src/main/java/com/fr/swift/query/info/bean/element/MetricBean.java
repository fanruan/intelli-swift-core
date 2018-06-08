package com.fr.swift.query.info.bean.element;

import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Lyon on 2018/6/2.
 */
public class MetricBean {

    @JsonProperty
    private AggregatorType type;
    @JsonProperty
    private String name;
    @JsonProperty
    private String fieldName;
    @JsonProperty
    private FilterInfoBean filterInfoBean;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
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
}
