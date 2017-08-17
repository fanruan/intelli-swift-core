package com.fr.bi.property;

/**
 * This class created on 2017/8/16.
 *
 * @author Each.Zhang
 */
public class PropertiesConfig {
    //属性对应的key(国际化)
    private String propertyKey;
    //属性名
    private String propertyName;
    //值类型
    private Class valueType;
    //值
    private Object value;
    //属性的标题
    private String title;
    //该属性的作用描述
    private String description;

    public PropertiesConfig() {
    }

    public PropertiesConfig(String propertyKey, String propertyName, Class valueType, Object value, String title, String description) {
        this.propertyKey = propertyKey;
        this.propertyName = propertyName;
        this.valueType = valueType;
        this.value = value;
        this.title = title;
        this.description = description;
    }

    public String getPropertyKey() {
        return propertyKey;
    }

    public void setPropertyKey(String propertyKey) {
        this.propertyKey = propertyKey;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public Class getValueType() {
        return valueType;
    }

    public void setValueType(Class valueType) {
        this.valueType = valueType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
