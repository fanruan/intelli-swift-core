package com.fr.swift.query.info.bean.element;

import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;

/**
 * Created by Lyon on 2018/6/2.
 */
public class MetricBean {

    @JsonProperty
    private AggregatorType type;
    @JsonProperty
    private String column;  // 原始表中的字段名
    @JsonProperty
    private String alias;    // 客户端定义的转移名
    @JsonProperty
    private FilterInfoBean filter;
    @JsonProperty
    private Object[] params;

    public MetricBean(String column, AggregatorType type) {
        this.type = type;
        this.column = column;
    }

    public MetricBean() {
    }

    public AggregatorType getType() {
        return type;
    }

    public void setType(AggregatorType type) {
        this.type = type;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public FilterInfoBean getFilter() {
        return filter;
    }

    public void setFilter(FilterInfoBean filter) {
        this.filter = filter;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public static Builder builder(String column, AggregatorType type) {
        return new Builder(column, type);
    }

    public static class Builder {
        private MetricBean bean;

        public Builder(String column, AggregatorType type) {
            bean = new MetricBean(column, type);
        }

        public Builder setAlias(String alias) {
            bean.setAlias(alias);
            return this;
        }

        public Builder setFilter(FilterInfoBean filter) {
            bean.setFilter(filter);
            return this;
        }

        public Builder setParams(Object[] params) {
            bean.setParams(params);
            return this;
        }

        public MetricBean build() {
            return bean;
        }
    }
}
