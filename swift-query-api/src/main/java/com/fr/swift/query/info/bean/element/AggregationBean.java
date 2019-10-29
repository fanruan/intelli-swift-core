package com.fr.swift.query.info.bean.element;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.funnel.FunnelAggregationBean;
import com.fr.swift.query.info.funnel.FunnelPathsAggregationBean;

/**
 * @author yee
 * @date 2019-06-28
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        property = "type",
        defaultImpl = AggregatorType.class)
@JsonSubTypes(value = {
        @JsonSubTypes.Type(name = "FUNNEL", value = FunnelAggregationBean.class),
        @JsonSubTypes.Type(name = "FUNNEL_PATHS", value = FunnelPathsAggregationBean.class),
        @JsonSubTypes.Type(name = "SUM", value = MetricBean.class),
        @JsonSubTypes.Type(name = "MAX", value = MetricBean.class),
        @JsonSubTypes.Type(name = "MIN", value = MetricBean.class),
        @JsonSubTypes.Type(name = "AVERAGE", value = MetricBean.class),
        @JsonSubTypes.Type(name = "COUNT", value = MetricBean.class),
        @JsonSubTypes.Type(name = "DISTINCT", value = MetricBean.class),
        @JsonSubTypes.Type(name = "HLL_DISTINCT", value = MetricBean.class),
        @JsonSubTypes.Type(name = "STRING_COMBINE", value = MetricBean.class),
        @JsonSubTypes.Type(name = "DATE_MAX", value = MetricBean.class),
        @JsonSubTypes.Type(name = "DATE_MIN", value = MetricBean.class),
        @JsonSubTypes.Type(name = "MEDIAN", value = MetricBean.class),
        @JsonSubTypes.Type(name = "VARIANCE", value = MetricBean.class),
        @JsonSubTypes.Type(name = "STANDARD_DEVIATION", value = MetricBean.class),
        @JsonSubTypes.Type(name = "DUMMY", value = MetricBean.class),
        @JsonSubTypes.Type(name = "DISTINCT_DATE_YMD", value = MetricBean.class),
        @JsonSubTypes.Type(name = "TOP_PERCENTILE", value = MetricBean.class)
})
public interface AggregationBean {
    AggregatorType getType();

    String getColumn();

    Object[] getParams();

    String getAlias();

    FilterInfoBean getFilter();

    void setAlias(String alias);
}
