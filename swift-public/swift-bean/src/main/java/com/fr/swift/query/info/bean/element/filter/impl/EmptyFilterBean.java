package com.fr.swift.query.info.bean.element.filter.impl;

import com.fr.swift.query.filter.SwiftDetailFilterType;

import java.io.Serializable;

/**
 * Created by Lyon on 2018/7/26.
 */
public class EmptyFilterBean extends DetailFilterInfoBean<Object> implements Serializable {

    private static final long serialVersionUID = 7151646175270442582L;

    {
        type = SwiftDetailFilterType.EMPTY;
    }

    @Override
    public Object getFilterValue() {
        return null;
    }

    @Override
    public void setFilterValue(Object filterValue) {

    }
}
