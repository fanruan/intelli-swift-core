package com.fr.swift.query.info.bean.query;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.query.QueryType;

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
        @JsonSubTypes.Type(value = FunnelQueryBean.class, name = "FUNNEL")

})
public interface QueryInfoBean extends QueryBean {
}
