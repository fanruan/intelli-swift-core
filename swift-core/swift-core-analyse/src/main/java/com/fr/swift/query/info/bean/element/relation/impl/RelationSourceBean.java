package com.fr.swift.query.info.bean.element.relation.impl;

import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.query.info.bean.element.relation.IRelationSourceBean;
import com.fr.swift.source.RelationSourceType;

import java.util.List;

/**
 * @author yee
 * @date 2018/6/26
 */
public class RelationSourceBean implements IRelationSourceBean {

    @JsonProperty
    private RelationSourceType type;
    @JsonProperty
    private String primaryTable;
    @JsonProperty
    private String foreignTable;
    @JsonProperty
    private List<String> primaryFields;
    @JsonProperty
    private List<String> foreignFields;


    public RelationSourceBean() {
        this(RelationSourceType.RELATION);
    }

    public RelationSourceBean(RelationSourceType type) {
        this.type = type;
    }

    @Override
    public RelationSourceType getType() {
        return type;
    }

    public void setType(RelationSourceType type) {
        this.type = type;
    }

    public String getPrimaryTable() {
        return primaryTable;
    }

    public void setPrimaryTable(String primaryTable) {
        this.primaryTable = primaryTable;
    }

    public String getForeignTable() {
        return foreignTable;
    }

    public void setForeignTable(String foreignTable) {
        this.foreignTable = foreignTable;
    }

    public List<String> getPrimaryFields() {
        return primaryFields;
    }

    public void setPrimaryFields(List<String> primaryFields) {
        this.primaryFields = primaryFields;
    }

    public List<String> getForeignFields() {
        return foreignFields;
    }

    public void setForeignFields(List<String> foreignFields) {
        this.foreignFields = foreignFields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RelationSourceBean that = (RelationSourceBean) o;

        if (type != that.type) return false;
        if (primaryTable != null ? !primaryTable.equals(that.primaryTable) : that.primaryTable != null) return false;
        if (foreignTable != null ? !foreignTable.equals(that.foreignTable) : that.foreignTable != null) return false;
        if (primaryFields != null ? !primaryFields.equals(that.primaryFields) : that.primaryFields != null)
            return false;
        return foreignFields != null ? foreignFields.equals(that.foreignFields) : that.foreignFields == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (primaryTable != null ? primaryTable.hashCode() : 0);
        result = 31 * result + (foreignTable != null ? foreignTable.hashCode() : 0);
        result = 31 * result + (primaryFields != null ? primaryFields.hashCode() : 0);
        result = 31 * result + (foreignFields != null ? foreignFields.hashCode() : 0);
        return result;
    }
}
