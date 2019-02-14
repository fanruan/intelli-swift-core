package com.fr.swift.query.info.bean.element.filter.impl;

import com.fr.swift.query.filter.SwiftDetailFilterType;

import java.io.Serializable;

/**
 * Created by Lyon on 2018/7/26.
 */
public class AllShowFilterBean extends DetailFilterInfoBean<Object> implements Serializable {

    private static final long serialVersionUID = -2929362246689385433L;

    {
        type = SwiftDetailFilterType.ALL_SHOW;
    }

    @Override
    public Object getFilterValue() {
        return null;
    }

    @Override
    public void setFilterValue(Object filterValue) {

    }
}
