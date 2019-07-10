package com.fr.swift.query.info.bean.element.relation.impl;

import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.query.info.bean.element.relation.IRelationSourceBean;
import com.fr.swift.source.RelationSourceType;

/**
 * @author yee
 * @date 2018/6/26
 */
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        FieldRelationSourceBean that = (FieldRelationSourceBean) o;

        if (columnName != null ? !columnName.equals(that.columnName) : that.columnName != null) return false;
        return columnRelation != null ? columnRelation.equals(that.columnRelation) : that.columnRelation == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (columnName != null ? columnName.hashCode() : 0);
        result = 31 * result + (columnRelation != null ? columnRelation.hashCode() : 0);
        return result;
    }
}
