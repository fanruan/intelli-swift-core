package com.fr.swift.query.info.bean.element.filter.impl;

import java.util.Set;

/**
 * Created by Lyon on 2018/6/28.
 */
public class NumberInFilterBean extends DetailFilterInfoBean<Set<Double>> {
    @Override
    public Set<Double> getFilterValue() {
        return filterValue;
    }

    @Override
    public void setFilterValue(Set<Double> filterValue) {
        this.filterValue = filterValue;
    }
}
