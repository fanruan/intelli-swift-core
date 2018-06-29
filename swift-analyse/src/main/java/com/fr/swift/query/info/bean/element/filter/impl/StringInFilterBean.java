package com.fr.swift.query.info.bean.element.filter.impl;

import java.util.Set;

/**
 * Created by Lyon on 2018/6/28.
 */
public class StringInFilterBean extends DetailFilterInfoBean<Set<String>> {
    @Override
    public Set<String> getFilterValue() {
        return filterValue;
    }

    @Override
    public void setFilterValue(Set<String> filterValue) {
        this.filterValue = filterValue;
    }
}
