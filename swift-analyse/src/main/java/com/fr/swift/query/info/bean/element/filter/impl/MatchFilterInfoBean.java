package com.fr.swift.query.info.bean.element.filter.impl;

import com.fr.swift.query.info.bean.element.filter.AbstractFilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author yee
 * @date 2018/6/22
 */
public class MatchFilterInfoBean extends AbstractFilterInfoBean {

    @JsonProperty
    private FilterInfoBean filterInfoBean;
    @JsonProperty
    private int index;


    public MatchFilterInfoBean() {
        super(BeanType.MATCH);
    }

    public FilterInfoBean getFilterInfoBean() {
        return filterInfoBean;
    }

    public void setFilterInfoBean(FilterInfoBean filterInfoBean) {
        this.filterInfoBean = filterInfoBean;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
