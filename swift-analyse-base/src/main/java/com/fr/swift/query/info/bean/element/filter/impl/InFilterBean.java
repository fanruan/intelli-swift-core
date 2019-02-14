package com.fr.swift.query.info.bean.element.filter.impl;

import com.fr.swift.query.filter.SwiftDetailFilterType;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Lyon on 2018/6/28.
 */
public class InFilterBean extends DetailFilterInfoBean<Set<String>> implements Serializable {

    private static final long serialVersionUID = -5714388508922623532L;

    {
        type = SwiftDetailFilterType.IN;
    }

    public InFilterBean() {
    }

    public InFilterBean(String column, Object... values) {
        setColumn(column);
        this.filterValue = new HashSet<String>();
        for (Object value : values) {
            if (value != null) {
                filterValue.add(value.toString());
            }
        }
    }

    @Override
    public Set<String> getFilterValue() {
        return filterValue;
    }

    @Override
    public void setFilterValue(Set<String> filterValue) {
        this.filterValue = filterValue;
    }
}
