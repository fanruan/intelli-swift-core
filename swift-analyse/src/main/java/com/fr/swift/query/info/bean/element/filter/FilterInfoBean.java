package com.fr.swift.query.info.bean.element.filter;

import com.fr.swift.query.info.bean.element.filter.impl.DetailFilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.GeneralFilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.MatchFilterInfoBean;
import com.fr.third.fasterxml.jackson.annotation.JsonSubTypes;
import com.fr.third.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author yee
 * @date 2018/6/22
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "beanType",
        defaultImpl = FilterInfoBean.BeanType.class,
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DetailFilterInfoBean.class, name = "DETAIL"),
        @JsonSubTypes.Type(value = GeneralFilterInfoBean.class, name = "GENERAL"),
        @JsonSubTypes.Type(value = MatchFilterInfoBean.class, name = "MATCH")
})
public interface FilterInfoBean {

    BeanType getBeanType();

    enum BeanType {
        GENERAL, DETAIL, MATCH
    }
}
