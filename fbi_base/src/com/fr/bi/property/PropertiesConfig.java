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
    //private String valueType;
    private ValueType valueType;
    //值生效类型，是否需要重启
    //private String availableType;
    private AvaliableType avaliableType;
    //属性的标题
    private String title;
    //该属性的作用描述
    private String description;

    public PropertiesConfig() {
    }

    public PropertiesConfig(String propertyKey, String propertyName, ValueType valueType, AvaliableType avaliableType, String title, String description) {
        this.propertyKey = propertyKey;
        this.propertyName = propertyName;
        this.valueType = valueType;
        this.avaliableType = avaliableType;
        this.title = title;
        this.description = description;
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
}
