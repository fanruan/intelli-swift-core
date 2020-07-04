package com.fr.swift.query.info.bean.element;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.util.Strings;

/**
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

    public MetricBean(String column, AggregatorType type, String alias) {
        this.type = type;
        this.column = column;
        this.alias = alias;
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

    public static MetricBean ofAverage(String column) {
        return new MetricBean.Builder().setType(AggregatorType.AVERAGE)
                .setColumn(column).setAlias(Strings.toHumpString(AggregatorType.AVERAGE.toString(), column)).build();
    }

    public static MetricBean ofAverage(String column, String alias) {
        return new MetricBean.Builder().setType(AggregatorType.AVERAGE).setColumn(column).setAlias(alias).build();
    }

    public static MetricBean ofCount(String column) {
        return new MetricBean.Builder().setType(AggregatorType.COUNT)
                .setColumn(column).setAlias(Strings.toHumpString(AggregatorType.COUNT.toString(), column)).build();
    }

    public static MetricBean ofCount(String column, String alias) {
        return new MetricBean.Builder().setType(AggregatorType.COUNT).setColumn(column).setAlias(alias).build();
    }

    public static MetricBean ofDistinct(String column) {
        return new MetricBean.Builder().setType(AggregatorType.DISTINCT)
                .setColumn(column).setAlias(Strings.toHumpString(AggregatorType.DISTINCT.toString(), column)).build();
    }

    public static MetricBean ofDistinct(String column, String alias) {
        return new MetricBean.Builder().setType(AggregatorType.DISTINCT).setColumn(column).setAlias(alias).build();
    }

    public static MetricBean ofMax(String column) {
        return new MetricBean.Builder().setType(AggregatorType.MAX).setColumn(column)
                .setAlias(Strings.toHumpString(AggregatorType.MAX.toString(), column)).build();
    }

    public static MetricBean ofMax(String column, String alias) {
        return new MetricBean.Builder().setType(AggregatorType.MAX).setColumn(column).setAlias(alias).build();
    }

    public static MetricBean ofMin(String column) {
        return new MetricBean.Builder().setType(AggregatorType.MIN).setColumn(column)
                .setAlias(Strings.toHumpString(AggregatorType.MIN.toString(), column)).build();
    }

    public static MetricBean ofMin(String column, String alias) {
        return new MetricBean.Builder().setType(AggregatorType.MIN).setColumn(column).setAlias(alias).build();
    }

    public static MetricBean ofSum(String column) {
        return new MetricBean.Builder().setType(AggregatorType.SUM).setColumn(column)
                .setAlias(Strings.toHumpString(AggregatorType.SUM.toString(), column)).build();
    }

    public static MetricBean ofSum(String column, String alias) {
        return new MetricBean.Builder().setType(AggregatorType.SUM).setColumn(column).setAlias(alias).build();
    }


    public static MetricBean ofMedian(String column) {
        return new MetricBean.Builder().setType(AggregatorType.MEDIAN).setColumn(column)
                .setAlias(Strings.toHumpString(AggregatorType.MEDIAN.toString(), column)).build();
    }

    public static MetricBean ofMedian(String column, String alias) {
        return new MetricBean.Builder().setType(AggregatorType.MEDIAN).setColumn(column).setAlias(alias).build();
    }

    public static Builder builder(String column, AggregatorType type) {
        return new Builder(column, type);
    }

    public static class Builder {
        private MetricBean bean;

        public Builder() {
            bean = new MetricBean();
        }

        public Builder(String column, AggregatorType type) {
            bean = new MetricBean(column, type);
        }

        public Builder setType(AggregatorType type) {
            bean.setType(type);
            return this;
        }

        public Builder setColumn(String column) {
            bean.setColumn(column);
            return this;
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
