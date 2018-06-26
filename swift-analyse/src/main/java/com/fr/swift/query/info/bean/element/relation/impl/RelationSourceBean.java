package com.fr.swift.query.info.bean.element.relation.impl;

import com.fr.swift.query.info.bean.element.relation.IRelationSourceBean;
import com.fr.swift.source.RelationSourceType;
import com.fr.third.fasterxml.jackson.annotation.JsonInclude;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author yee
 * @date 2018/6/26
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
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
}
