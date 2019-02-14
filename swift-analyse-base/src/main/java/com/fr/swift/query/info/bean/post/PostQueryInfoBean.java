package com.fr.swift.query.info.bean.post;

import com.fr.swift.base.json.annotation.JsonSubTypes;
import com.fr.swift.base.json.annotation.JsonTypeInfo;
import com.fr.swift.query.info.bean.type.PostQueryType;

/**
 * Created by Lyon on 2018/6/3.
 */
@JsonTypeInfo(
        property = "type",
        defaultImpl = PostQueryType.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CalculatedFieldQueryInfoBean.class, name = "CAL_FIELD"),
        @JsonSubTypes.Type(value = HavingFilterQueryInfoBean.class, name = "HAVING_FILTER"),
        @JsonSubTypes.Type(value = TreeFilterQueryInfoBean.class, name = "TREE_FILTER"),
        @JsonSubTypes.Type(value = TreeAggregationQueryInfoBean.class, name = "TREE_AGGREGATION"),
        @JsonSubTypes.Type(value = TreeSortQueryInfoBean.class, name = "TREE_SORT"),
        @JsonSubTypes.Type(value = RowSortQueryInfoBean.class, name = "ROW_SORT"),
        @JsonSubTypes.Type(value = FunnelMedianInfoBean.class, name = "FUNNEL_MEDIAN")
})
public interface PostQueryInfoBean {

    PostQueryType getType();
}
