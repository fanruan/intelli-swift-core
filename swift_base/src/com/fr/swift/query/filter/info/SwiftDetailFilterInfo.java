package com.fr.swift.query.filter.info;

import com.fr.swift.query.filter.DetailFilterFactory;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
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

    @CoreField
    private ColumnKey columnKey;

    public SwiftDetailFilterInfo(ColumnKey columnKey, T filterValue, SwiftDetailFilterType type) {
        this.filterValue = filterValue;
        this.type = type;
        this.columnKey = columnKey;
        if (null != columnKey) {
            this.fieldName = columnKey.getName();
        }
    }

    public String getFieldName() {
        return fieldName;
    }

    public T getFilterValue() {
        return filterValue;
    }

    public ColumnKey getColumnKey() {
        return columnKey;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SwiftDetailFilterInfo<?> that = (SwiftDetailFilterInfo<?>) o;

        if (fieldName != null ? !fieldName.equals(that.fieldName) : that.fieldName != null) return false;
        if (filterValue != null ? !filterValue.equals(that.filterValue) : that.filterValue != null) return false;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        int result = fieldName != null ? fieldName.hashCode() : 0;
        result = 31 * result + (filterValue != null ? filterValue.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
