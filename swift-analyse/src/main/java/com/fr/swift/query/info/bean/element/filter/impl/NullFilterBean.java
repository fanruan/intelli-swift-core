package com.fr.swift.query.info.bean.element.filter.impl;

import com.fr.swift.query.filter.SwiftDetailFilterType;

/**
 * Created by Lyon on 2018/6/28.
 */
public class NullFilterBean extends DetailFilterInfoBean<Object> {

    {
        type = SwiftDetailFilterType.NULL;
    }

    @Override
    public Object getFilterValue() {
        return null;
    }

    @Override
    public void setFilterValue(Object filterValue) {

    }
}
