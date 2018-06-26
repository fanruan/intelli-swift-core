package com.fr.swift.query.info.bean.element.filter.impl;

import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.info.bean.element.filter.AbstractFilterInfoBean;
import com.fr.swift.query.info.bean.element.relation.IRelationSourceBean;
import com.fr.third.fasterxml.jackson.annotation.JsonInclude;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Lyon on 2018/6/2.
 */
// TODO: 2018/6/8
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DetailFilterInfoBean extends AbstractFilterInfoBean {

    @JsonProperty
    private SwiftDetailFilterType type;

    @JsonProperty
    private Object filterValue;

    @JsonProperty
    private String column;

    @JsonProperty
    private IRelationSourceBean relation;

    public DetailFilterInfoBean() {
        super(BeanType.DETAIL);
    }

    public SwiftDetailFilterType getType() {
        return type;
    }

    public void setType(SwiftDetailFilterType type) {
        this.type = type;
    }

    public Object getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(Object filterValue) {
        this.filterValue = filterValue;
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
