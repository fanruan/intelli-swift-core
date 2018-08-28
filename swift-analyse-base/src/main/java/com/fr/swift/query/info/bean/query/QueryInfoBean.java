package com.fr.swift.query.info.bean.query;

import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.query.QueryType;
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
        @JsonSubTypes.Type(value = DetailQueryInfoBean.class, name = "DETAIL"),
        @JsonSubTypes.Type(value = GroupQueryInfoBean.class, name = "GROUP"),
        @JsonSubTypes.Type(value = ResultJoinQueryInfoBean.class, name = "RESULT_JOIN"),
        // TODO: 2018/6/27 下面三个类型按道理不应该在这用的
        @JsonSubTypes.Type(value = GroupQueryInfoBean.class, name = "LOCAL_GROUP_ALL"),
        @JsonSubTypes.Type(value = GroupQueryInfoBean.class, name = "LOCAL_GROUP_PART"),
        @JsonSubTypes.Type(value = DetailQueryInfoBean.class, name = "LOCAL_DETAIL")
})
public interface QueryInfoBean extends QueryBean {
}
