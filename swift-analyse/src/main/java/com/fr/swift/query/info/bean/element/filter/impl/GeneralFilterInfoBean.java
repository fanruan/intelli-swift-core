package com.fr.swift.query.info.bean.element.filter.impl;

import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.third.fasterxml.jackson.annotation.JsonInclude;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author yee
 * @date 2018/6/22
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class GeneralFilterInfoBean<T> implements FilterInfoBean {

    @JsonProperty
    protected T filterValue;
    @JsonProperty
    private SwiftDetailFilterType type;

    public abstract T getFilterValue();

    public abstract void setFilterValue(T filterValue);

    @Override
    public SwiftDetailFilterType getType() {
        return type;
    }

    public void setType(SwiftDetailFilterType type) {
        this.type = type;
    }
}
