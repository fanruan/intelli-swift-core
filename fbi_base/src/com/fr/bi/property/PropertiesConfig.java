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
    private String valueType;
    //值生效类型，是否需要重启
    private String availableType;
    //属性的标题
    private String title;
    //该属性的作用描述
    private String description;

    public PropertiesConfig() {
    }

    public PropertiesConfig(String propertyKey, String propertyName, String valueType, String availableType, String title, String description) {
        this.propertyKey = propertyKey;
        this.propertyName = propertyName;
        this.valueType = valueType;
        this.availableType = availableType;
        this.title = title;
        this.description = description;
    }

    public String getPropertyKey() {
        return propertyKey;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getValueType() {
        return valueType;
    }

    public String getAvailableType() {
        return availableType;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
