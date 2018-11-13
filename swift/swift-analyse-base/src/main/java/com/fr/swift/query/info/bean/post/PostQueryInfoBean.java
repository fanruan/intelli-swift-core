package com.fr.swift.query.info.bean.post;

import com.fr.swift.query.info.bean.type.PostQueryType;
import com.fr.third.fasterxml.jackson.annotation.JsonSubTypes;
import com.fr.third.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Created by Lyon on 2018/6/3.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        defaultImpl = PostQueryType.class,
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CalculatedFieldQueryInfoBean.class, name = "CAL_FIELD"),
        @JsonSubTypes.Type(value = HavingFilterQueryInfoBean.class, name = "HAVING_FILTER"),
        @JsonSubTypes.Type(value = TreeFilterQueryInfoBean.class, name = "TREE_FILTER"),
        @JsonSubTypes.Type(value = TreeAggregationQueryInfoBean.class, name = "TREE_AGGREGATION"),
        @JsonSubTypes.Type(value = TreeSortQueryInfoBean.class, name = "TREE_SORT"),
        @JsonSubTypes.Type(value = RowSortQueryInfoBean.class, name = "ROW_SORT")
})
public interface PostQueryInfoBean {

    PostQueryType getType();
}
