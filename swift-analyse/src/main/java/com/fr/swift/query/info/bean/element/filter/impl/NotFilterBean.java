package com.fr.swift.query.info.bean.element.filter.impl;

import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;

/**
 * Created by Lyon on 2018/6/28.
 */
public class NotFilterBean extends GeneralFilterInfoBean<FilterInfoBean> {
    @Override
    public FilterInfoBean getFilterValue() {
        return filterValue;
    }

    @Override
    public void setFilterValue(FilterInfoBean filterValue) {
        this.filterValue = filterValue;
    }
}
