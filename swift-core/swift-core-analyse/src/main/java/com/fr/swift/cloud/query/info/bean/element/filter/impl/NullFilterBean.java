package com.fr.swift.cloud.query.info.bean.element.filter.impl;

import com.fr.swift.cloud.query.filter.SwiftDetailFilterType;

import java.io.Serializable;

/**
 * Created by Lyon on 2018/6/28.
 */
public class NullFilterBean extends DetailFilterInfoBean<Object> implements Serializable {

    private static final long serialVersionUID = -569684451986072800L;

    {
        type = SwiftDetailFilterType.NULL;
    }

    private NullFilterBean() {
    }

    public NullFilterBean(String columnName) {
        this.setColumn(columnName);
    }

    @Override
    public Object getFilterValue() {
        return null;
    }

    @Override
    public void setFilterValue(Object filterValue) {

    }
}
