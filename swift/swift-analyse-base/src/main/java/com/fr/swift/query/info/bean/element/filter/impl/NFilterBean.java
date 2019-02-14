package com.fr.swift.query.info.bean.element.filter.impl;

import com.fr.swift.query.filter.SwiftDetailFilterType;

import java.io.Serializable;

/**
 * Created by Lyon on 2018/6/28.
 */
public class NFilterBean extends DetailFilterInfoBean<Integer> implements Serializable {

    private static final long serialVersionUID = 5745104332814069864L;

    @Override
    public Integer getFilterValue() {
        return filterValue;
    }

    @Override
    public void setFilterValue(Integer filterValue) {
        this.filterValue = filterValue;
    }

    public static NFilterBean topN(String column, int n) {
        NFilterBean nFilterBean = new NFilterBean();
        nFilterBean.setType(SwiftDetailFilterType.TOP_N);
        nFilterBean.setColumn(column);
        nFilterBean.setFilterValue(n);
        return nFilterBean;
    }

    public static NFilterBean bottomN(String column, int n) {
        NFilterBean nFilterBean = new NFilterBean();
        nFilterBean.setType(SwiftDetailFilterType.BOTTOM_N);
        nFilterBean.setColumn(column);
        nFilterBean.setFilterValue(n);
        return nFilterBean;
    }
}
