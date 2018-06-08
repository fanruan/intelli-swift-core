package com.fr.swift.query.info.bean.query;

import com.fr.swift.query.QueryType;
import com.fr.third.fasterxml.jackson.annotation.JsonSubTypes;
import com.fr.third.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Created by Lyon on 2018/6/3.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "queryType",
        defaultImpl = QueryType.class,
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DetailQueryBean.class, name = "DETAIL"),
        @JsonSubTypes.Type(value = GroupQueryBean.class, name = "GROUP"),
        @JsonSubTypes.Type(value = ResultJoinQueryBean.class, name = "RESULT_JOIN")
})
public interface QueryBean {

    QueryType getQueryType();
}
