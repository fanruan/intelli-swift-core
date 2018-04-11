package com.fr.swift.query.filter.info;

import com.fr.swift.query.filter.DetailFilterFactory;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.core.CoreField;

/**
 * Created by Lyon on 2018/2/2.
 * 一个过滤器的过滤信息
 */
public class SwiftDetailFilterInfo<T> extends AbstractDetailFilterInfo {

    @CoreField
    private String fieldName;
    @CoreField
    private T filterValue;
    @CoreField
    private SwiftDetailFilterType type;

    public SwiftDetailFilterInfo(String fieldName, T filterValue, SwiftDetailFilterType type) {
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

    @Override
    public boolean isMatchFilter() {
        return false;
    }

    @Override
    public DetailFilter createDetailFilter(Segment segment) {
        return DetailFilterFactory.createFilter(segment, this);
    }
}
