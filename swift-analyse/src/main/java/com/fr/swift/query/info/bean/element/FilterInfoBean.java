package com.fr.swift.query.info.bean.element;

import com.fr.third.fasterxml.jackson.annotation.JsonSubTypes;
import com.fr.third.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author yee
 * @date 2018/6/22
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DetailFilterInfoBean.class, name = "DetailFilterInfoBean"),
        @JsonSubTypes.Type(value = GeneralFilterInfoBean.class, name = "GeneralFilterInfoBean"),
        @JsonSubTypes.Type(value = MatchFilterInfoBean.class, name = "MatchFilterInfoBean")
})
public interface FilterInfoBean {
}
