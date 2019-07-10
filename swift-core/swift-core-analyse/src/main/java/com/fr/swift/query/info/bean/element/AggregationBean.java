package com.fr.swift.query.info.bean.element;

import com.fr.swift.base.json.annotation.JsonSubTypes;
import com.fr.swift.base.json.annotation.JsonTypeInfo;
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
        defaultImpl = MetricBean.class)
@JsonSubTypes(value = {
        @JsonSubTypes.Type(name = "FUNNEL", value = FunnelAggregationBean.class),
        @JsonSubTypes.Type(name = "FUNNEL_PATHS", value = FunnelPathsAggregationBean.class)
})
public interface AggregationBean {
    AggregatorType getType();

    String getColumn();

    Object[] getParams();

    String getAlias();

    FilterInfoBean getFilter();
}
