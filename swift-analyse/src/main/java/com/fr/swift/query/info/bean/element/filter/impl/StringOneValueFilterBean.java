package com.fr.swift.query.info.bean.element.filter.impl;

/**
 * Created by Lyon on 2018/6/28.
 */
public class StringOneValueFilterBean extends DetailFilterInfoBean<String> {
    @Override
    public String getFilterValue() {
        return filterValue;
    }

    @Override
    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }
}
