package com.fr.swift.query.info.bean.element.filter.impl;

import java.io.Serializable;

/**
 * Created by Lyon on 2018/6/28.
 */
public class StringOneValueFilterBean extends DetailFilterInfoBean<String> implements Serializable {

    private static final long serialVersionUID = 7390765671897464448L;

    @Override
    public String getFilterValue() {
        return filterValue;
    }

    @Override
    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }
}
