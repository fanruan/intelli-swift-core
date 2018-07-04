package com.fr.swift.query.info.bean.element;

import com.fr.swift.query.info.bean.element.relation.IRelationSourceBean;
import com.fr.swift.query.sort.SortType;
import com.fr.third.fasterxml.jackson.annotation.JsonInclude;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Lyon on 2018/6/2.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SortBean {

    @JsonProperty
    private SortType type;
    @JsonProperty
    private String column;
    @JsonProperty
    private IRelationSourceBean relation;

    public SortBean() {
    }

    public SortType getType() {
        return type;
    }

    public void setType(SortType type) {
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
