package com.fr.swift.query.info.bean.element.relation.impl;

import com.fr.swift.query.info.bean.element.relation.IRelationSourceBean;
import com.fr.swift.source.RelationSourceType;
import com.fr.third.fasterxml.jackson.annotation.JsonInclude;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author yee
 * @date 2018/6/26
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldRelationSourceBean extends RelationSourceBean {

    @JsonProperty
    private String columnName;
    @JsonProperty
    private IRelationSourceBean columnRelation;

    public FieldRelationSourceBean() {
        super(RelationSourceType.FIELD_RELATION);
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public IRelationSourceBean getColumnRelation() {
        return columnRelation;
    }

    public void setColumnRelation(IRelationSourceBean columnRelation) {
        this.columnRelation = columnRelation;
    }
}
