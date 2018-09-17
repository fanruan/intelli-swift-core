package com.fr.swift.query.info.bean.element.filter.impl;

import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.relation.IRelationSourceBean;
import com.fr.swift.util.qm.bool.BExprType;
import com.fr.swift.util.qm.bool.BVar;
import com.fr.third.fasterxml.jackson.annotation.JsonInclude;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Lyon on 2018/6/2.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class DetailFilterInfoBean<T> extends BVar implements FilterInfoBean<T> {

    private static final long serialVersionUID = 2298764777503853299L;
    @JsonProperty
    protected SwiftDetailFilterType type;
    @JsonProperty
    protected T filterValue;
    @JsonProperty
    private String column;
    @JsonProperty
    private IRelationSourceBean relation;

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

    public IRelationSourceBean getRelation() {
        return relation;
    }

    public void setRelation(IRelationSourceBean relation) {
        this.relation = relation;
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
        if (column != null ? !column.equals(that.column) : that.column != null) return false;
        return relation != null ? relation.equals(that.relation) : that.relation == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (filterValue != null ? filterValue.hashCode() : 0);
        result = 31 * result + (column != null ? column.hashCode() : 0);
        result = 31 * result + (relation != null ? relation.hashCode() : 0);
        return result;
    }
}
