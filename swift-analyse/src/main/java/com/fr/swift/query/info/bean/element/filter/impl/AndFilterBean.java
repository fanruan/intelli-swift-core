package com.fr.swift.query.info.bean.element.filter.impl;

import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;

import java.util.List;

/**
 * Created by Lyon on 2018/6/28.
 */
public class AndFilterBean extends GeneralFilterInfoBean<List<FilterInfoBean>> {
    @Override
    public List<FilterInfoBean> getFilterValue() {
        return filterValue;
    }

    @Override
    public void setFilterValue(List<FilterInfoBean> filterValue) {
        this.filterValue = filterValue;
    }
}
