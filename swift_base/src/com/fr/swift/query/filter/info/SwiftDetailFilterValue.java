package com.fr.swift.query.filter.info;

import com.fr.swift.query.filter.SwiftDetailFilterType;

/**
 * Created by Lyon on 2018/2/2.
 * 一个过滤器的过滤信息
 */
public class SwiftDetailFilterValue<T> {

    private String fieldName;
    private T filterValue;
    private SwiftDetailFilterType type;

    public SwiftDetailFilterValue(String fieldName, T filterValue, SwiftDetailFilterType type) {
        this.fieldName = fieldName;
        this.filterValue = filterValue;
        this.type = type;
    }

    public String getFieldName() {
        return fieldName;
    }

    public T getFilterValue() {
        return filterValue;
    }

    public SwiftDetailFilterType getType() {
        return type;
    }
}
