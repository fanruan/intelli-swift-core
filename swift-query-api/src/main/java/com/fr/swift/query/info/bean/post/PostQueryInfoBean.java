package com.fr.swift.query.info.bean.post;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fr.swift.query.info.bean.type.PostQueryType;

/**
 * Created by Lyon on 2018/6/3.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",
        defaultImpl = PostQueryType.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CalculatedFieldQueryInfoBean.class, name = "CAL_FIELD"),
        @JsonSubTypes.Type(value = HavingFilterQueryInfoBean.class, name = "HAVING_FILTER"),
        @JsonSubTypes.Type(value = TreeFilterQueryInfoBean.class, name = "TREE_FILTER"),
        @JsonSubTypes.Type(value = TreeAggregationQueryInfoBean.class, name = "TREE_AGGREGATION"),
        @JsonSubTypes.Type(value = TreeSortQueryInfoBean.class, name = "TREE_SORT"),
        @JsonSubTypes.Type(value = RowSortQueryInfoBean.class, name = "ROW_SORT"),
        @JsonSubTypes.Type(value = FunnelPostInfoBean.class, name = "FUNNEL_TIME_MEDIAN"),
        @JsonSubTypes.Type(value = FunnelPostInfoBean.class, name = "FUNNEL_TIME_AVG"),
        @JsonSubTypes.Type(value = FunnelPostInfoBean.class, name = "FUNNEL_CONVERSION_RATE")
})
public interface PostQueryInfoBean {

    PostQueryType getType();
}
