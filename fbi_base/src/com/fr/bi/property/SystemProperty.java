package com.fr.bi.property;

import java.util.List;

public class SystemProperty {
    private String primaryKey;
    private String primaryName;
    private ValueType primaryValueType;
    private Object primaryValue;
    private AvaliableType primaryAvaliableType;
    private String primaryTitle;
    private String primaryDescription;
    private String foreignKey;
    private String foreignName;
    private ValueType foreignValueType;
    private Object foreignValue;
    private AvaliableType foreignAvaliableType;
    private String foreignTitle;
    private String foreignDescription;

    public SystemProperty() {
    }

    private SystemProperty(String primaryKey, String primaryName, ValueType primaryValueType, Object primaryValue, AvaliableType primaryAvaliableType, String primaryTitle, String primaryDescription) {
        this.primaryKey = primaryKey;
        this.primaryName = primaryName;
        this.primaryValueType = primaryValueType;
        this.primaryValue = primaryValue;
        this.primaryAvaliableType = primaryAvaliableType;
        this.primaryTitle = primaryTitle;
        this.primaryDescription = primaryDescription;
    }

    private SystemProperty(String primaryKey, String primaryName, ValueType primaryValueType, Object primaryValue, AvaliableType primaryAvaliableType, String primaryTitle, String primaryDescription, String foreignKey, String foreignName, ValueType foreignValueType, Object foreignValue, AvaliableType foreignAvaliableType, String foreignTitle, String foreignDescription) {
        this.primaryKey = primaryKey;
        this.primaryName = primaryName;
        this.primaryValueType = primaryValueType;
        this.primaryValue = primaryValue;
        this.primaryAvaliableType = primaryAvaliableType;
        this.primaryTitle = primaryTitle;
        this.primaryDescription = primaryDescription;
        this.foreignKey = foreignKey;
        this.foreignName = foreignName;
        this.foreignValueType = foreignValueType;
        this.foreignValue = foreignValue;
        this.foreignAvaliableType = foreignAvaliableType;
        this.foreignTitle = foreignTitle;
        this.foreignDescription = foreignDescription;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getPrimaryName() {
        return primaryName;
    }

    public void setPrimaryName(String primaryName) {
        this.primaryName = primaryName;
    }

    public ValueType getPrimaryValueType() {
        return primaryValueType;
    }

    public void setPrimaryValueType(ValueType primaryValueType) {
        this.primaryValueType = primaryValueType;
    }

    public Object getPrimaryValue() {
        return primaryValue;
    }

    public void setPrimaryValue(Object primaryValue) {
        this.primaryValue = primaryValue;
    }

    public AvaliableType getPrimaryAvaliableType() {
        return primaryAvaliableType;
    }

    public void setPrimaryAvaliableType(AvaliableType primaryAvaliableType) {
        this.primaryAvaliableType = primaryAvaliableType;
    }

    public String getPrimaryTitle() {
        return primaryTitle;
    }

    public void setPrimaryTitle(String primaryTitle) {
        this.primaryTitle = primaryTitle;
    }

    public String getPrimaryDescription() {
        return primaryDescription;
    }

    public void setPrimaryDescription(String primaryDescription) {
        this.primaryDescription = primaryDescription;
    }

    public String getForeignKey() {
        return foreignKey;
    }

    public void setForeignKey(String foreignKey) {
        this.foreignKey = foreignKey;
    }

    public String getForeignName() {
        return foreignName;
    }

    public void setForeignName(String foreignName) {
        this.foreignName = foreignName;
    }

    public ValueType getForeignValueType() {
        return foreignValueType;
    }

    public void setForeignValueType(ValueType foreignValueType) {
        this.foreignValueType = foreignValueType;
    }

    public Object getForeignValue() {
        return foreignValue;
    }

    public void setForeignValue(Object foreignValue) {
        this.foreignValue = foreignValue;
    }

    public AvaliableType getForeignAvaliableType() {
        return foreignAvaliableType;
    }

    public void setForeignAvaliableType(AvaliableType foreignAvaliableType) {
        this.foreignAvaliableType = foreignAvaliableType;
    }

    public String getForeignTitle() {
        return foreignTitle;
    }

    public void setForeignTitle(String foreignTitle) {
        this.foreignTitle = foreignTitle;
    }

    public String getForeignDescription() {
        return foreignDescription;
    }

    public void setForeignDescription(String foreignDescription) {
        this.foreignDescription = foreignDescription;
    }

    /**
     * 有参创建SystemProperty对象
     *
     * @param propertiesConfig
     * @return
     */
    public static SystemProperty constructObject(PropertiesConfig propertiesConfig) {
        return new SystemProperty(propertiesConfig.getPropertyKey(), propertiesConfig.getPropertyName(), propertiesConfig.getValueType(), propertiesConfig.getValue(), propertiesConfig.getAvaliableType(), propertiesConfig.getTitle(), propertiesConfig.getDescription());
    }

    /**
     * 根据relation关系，有参创建SystemProperty对象
     *
     * @param noRelationProperty
     * @param withRelationProperty
     * @return
     */
    public static SystemProperty construectObject(PropertiesConfig noRelationProperty, PropertiesConfig withRelationProperty) {
        return new SystemProperty(noRelationProperty.getPropertyKey(), noRelationProperty.getPropertyName(),noRelationProperty.getValueType(), noRelationProperty.getValue(), noRelationProperty.getAvaliableType(), noRelationProperty.getTitle(), noRelationProperty.getDescription(), withRelationProperty.getPropertyKey(), withRelationProperty.getPropertyName(), withRelationProperty.getValueType(), withRelationProperty.getValue(), withRelationProperty.getAvaliableType(), withRelationProperty.getTitle(), withRelationProperty.getDescription());
    }
}
