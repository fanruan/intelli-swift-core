package com.fr.bi.property;

/**
 * This class created on 2017/8/16.
 *
 * @author Each.Zhang
 */
public class PropertiesConfig {
    //属性对应的key(用于国际化配置)
    private String propertyKey;
    //属性名
    private String propertyName;
    //值类型
    private ValueType valueType;
    //值
    private Object value;
    //值生效类型，是否需要重启
    //private String availableType;
    private AvaliableType avaliableType;
    //属性的标题
    private String title;
    //该属性的作用描述
    private String description;
    //修改配置之后，关联的配置对应的key
    private String relationKey;

    public PropertiesConfig() {
    }

    public PropertiesConfig(String propertyKey, String propertyName, ValueType valueType, Object value, AvaliableType avaliableType, String title, String description, String relationKey) {
        this.propertyKey = propertyKey;
        this.propertyName = propertyName;
        this.valueType = valueType;
        this.value = value;
        this.avaliableType = avaliableType;
        this.title = title;
        this.description = description;
        this.relationKey = relationKey;
    }

    public String getPropertyKey() {
        return propertyKey;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public AvaliableType getAvaliableType() {
        return avaliableType;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getRelationKey() {
        return relationKey;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
