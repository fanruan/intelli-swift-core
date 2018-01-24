package com.fr.swift.conf.business.relation;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2018-1-24 11:25:51
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SimpleBusinessRelation {

    private String relationName;

    private List<String> primaryFields;

    private List<String> foreignFields;

    private String primaryTable;

    private String foreignTable;

    private int relationType;

    public SimpleBusinessRelation(String relationName, List<String> primaryFields, List<String> foreignFields, String primaryTable, String foreignTable, int relationType) {
        this.relationName = relationName;
        this.primaryFields = primaryFields;
        this.foreignFields = foreignFields;
        this.primaryTable = primaryTable;
        this.foreignTable = foreignTable;
        this.relationType = relationType;
    }

    public SimpleBusinessRelation(Map<String, Object> jsonMap) throws IOException {
        this.relationName = String.valueOf(jsonMap.get("relationName"));
        this.primaryTable = String.valueOf(jsonMap.get("primaryTable"));
        this.foreignTable = String.valueOf(jsonMap.get("foreignTable"));
        this.primaryFields = (List<String>) jsonMap.get("primaryFields");
        this.foreignFields = (List<String>) jsonMap.get("foreignFields");
        this.relationType = Integer.valueOf(String.valueOf(jsonMap.get("relationType")));
    }

    public String getRelationName() {
        return relationName;
    }

    public void setRelationName(String relationName) {
        this.relationName = relationName;
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

    public int getRelationType() {
        return relationType;
    }

    public void setRelationType(int relationType) {
        this.relationType = relationType;
    }
}
