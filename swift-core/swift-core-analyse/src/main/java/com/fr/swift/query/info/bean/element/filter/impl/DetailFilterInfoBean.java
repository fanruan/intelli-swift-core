package com.fr.swift.query.info.bean.element.filter.impl;

import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.util.qm.bool.BExprType;
import com.fr.swift.util.qm.bool.BVar;

/**
 * Created by Lyon on 2018/6/2.
 */
public abstract class DetailFilterInfoBean<T> extends BVar implements FilterInfoBean<T> {

    @JsonProperty
    protected SwiftDetailFilterType type;
    @JsonProperty
    protected T filterValue;
    @JsonProperty
    private String column;

    @Override
    public SwiftDetailFilterType getType() {
        return type;
    }

    @Override
    public void setType(SwiftDetailFilterType type) {
        this.type = type;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    @Override
    public BExprType type() {
        return BExprType.VAR;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DetailFilterInfoBean<?> that = (DetailFilterInfoBean<?>) o;

        if (type != that.type) return false;
        if (filterValue != null ? !filterValue.equals(that.filterValue) : that.filterValue != null) return false;
        return column != null ? !column.equals(that.column) : that.column != null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (filterValue != null ? filterValue.hashCode() : 0);
        result = 31 * result + (column != null ? column.hashCode() : 0);
        return result;
    }
}
