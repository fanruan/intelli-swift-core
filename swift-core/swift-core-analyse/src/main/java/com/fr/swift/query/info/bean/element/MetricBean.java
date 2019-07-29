package com.fr.swift.query.info.bean.element;

import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;

/**
 *
 * @author Lyon
 * @date 2018/6/2
 */
public class MetricBean implements AggregationBean {

    /**
     * 聚合类型
     */
    @JsonProperty
    private AggregatorType type;
    /**
     * 原始表中的字段名
     */
    @JsonProperty
    private String column;
    /**
     * 客户端定义的转译名
     */
    @JsonProperty
    private String alias;
    /**
     * 指标过滤
     */
    @JsonProperty
    private FilterInfoBean filter;
    /**
     * 聚合参数
     */
    @JsonProperty
    private Object[] params;

    public MetricBean(String column, AggregatorType type) {
        this.type = type;
        this.column = column;
    }

    public MetricBean() {
    }

    @Override
    public AggregatorType getType() {
        return type;
    }

    public void setType(AggregatorType type) {
        this.type = type;
    }

    @Override
    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public FilterInfoBean getFilter() {
        return filter;
    }

    public void setFilter(FilterInfoBean filter) {
        this.filter = filter;
    }

    @Override
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
