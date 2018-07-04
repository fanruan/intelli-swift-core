package com.fr.swift.query.info.bean.element.filter.impl;

import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.relation.IRelationSourceBean;
import com.fr.third.fasterxml.jackson.annotation.JsonInclude;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Lyon on 2018/6/2.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class DetailFilterInfoBean<T> implements FilterInfoBean<T> {

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
}
