package com.fr.bi.property;

import com.fr.general.ComparatorUtils;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLable;
import com.fr.stable.xml.XMLableReader;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2017/8/16.
 *
 * @author Each.Zhang
 */
public class PropertiesConfig implements XMLable{
    public static final String XML_TAG = "ConfigrationItem";
    public List<PropertiesConfig> propertyList = new ArrayList<PropertiesConfig>();
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
    private AvailableType availableType;
    //属性的标题
    private String title;
    //该属性的作用描述
    private String description;
    //修改配置之后，关联的配置对应的key
    private String relationKey;
    //是否提供修改
    private Modification isModified;

    public PropertiesConfig() {
    }

    public PropertiesConfig(String propertyKey, String propertyName, ValueType valueType, Object value, AvailableType availableType, String title, String description, String relationKey, Modification isModified) {
        this.propertyKey = propertyKey;
        this.propertyName = propertyName;
        this.valueType = valueType;
        this.value = value;
        this.availableType = availableType;
        this.title = title;
        this.description = description;
        this.relationKey = relationKey;
        this.isModified = isModified;
    }

    @Override
    public String toString() {
        return "{" +
                "\"propertyKey\":\"" + propertyKey + "\"," +
                "\"propertyName\":\"" + propertyName + "\"," +
                "\"valueType\":\"" + valueType + "\"," +
                "\"value\":\"" + value + "\"," +
                "\"availableType\":\"" + availableType + "\"," +
                "\"title\":\"" + title + "\"," +
                "\"description\":\"" + description + "\"," +
                "\"relationKey\":\"" + relationKey + "\"," +
                "\"isModified\":\"" + isModified + "\"" +
                "}";
    }


    @Override
    public void readXML(XMLableReader reader) {
        String tagName = reader.getTagName();
        String elementValue;
        if (reader.isChildNode()) {
            if (ComparatorUtils.equals(XML_TAG, tagName)) {
                if (checkNull()) {
                    propertyList.add(new PropertiesConfig(this.propertyKey, this.propertyName, this.valueType, this.value, this.availableType, this.title, this.description, this.relationKey, this.isModified));
                    this.relationKey = null;
                }
                this.setPropertyKey(reader.getAttrAsString("propertyKey", null));
            } else {
                if (ComparatorUtils.equals("PropertyName", tagName)) {
                    this.setPropertyName(reader.getElementValue());
                } else if (ComparatorUtils.equals("ValueType", tagName)) {
                    elementValue = reader.getElementValue();
                    if (ComparatorUtils.equals("Integer", elementValue)) {
                        this.setValueType(ValueType.INTEGER);
                    } else if (ComparatorUtils.equals("Double", elementValue)) {
                        this.setValueType(ValueType.DOUBLE);
                    } else if (ComparatorUtils.equals("String", elementValue)) {
                        this.setValueType(ValueType.STRING);
                    } else if (ComparatorUtils.equals("Boolean", elementValue)) {
                        this.setValueType(ValueType.BOOLEAN);
                    } else {
                        this.setValueType(ValueType.LONG);
                    }
                } else if (ComparatorUtils.equals("Value", tagName)) {
                    this.setValue(reader.getElementValue());
                } else if (ComparatorUtils.equals("AvailableType", tagName)) {
                    elementValue = reader.getElementValue();
                    if (ComparatorUtils.equals("Instant", elementValue)) {
                        this.setAvailableType(AvailableType.INSTANT);
                    } else {
                        this.setAvailableType(AvailableType.RESTART);
                    }
                } else if (ComparatorUtils.equals("Title", tagName)) {
                    this.setTitle(reader.getElementValue());
                } else if (ComparatorUtils.equals("Description", tagName)){
                    this.setDescription(reader.getElementValue());
                } else if (ComparatorUtils.equals("RelationKey", tagName)) {
                    this.setRelationKey(reader.getElementValue());
                } else if (ComparatorUtils.equals("IsModified", tagName)) {
                    elementValue = reader.getElementValue();
                    if (ComparatorUtils.equals(elementValue, "Show")) {
                        this.isModified = Modification.SHOW;
                    } else {
                        this.isModified = Modification.HIDDEN;
                    }
                }
            }
        }
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        writer.attr("propertyKey", this.propertyKey);
        writer.startTAG("ValueType").textNode(String.valueOf(this.valueType));
        writer.startTAG("Value").textNode(String.valueOf(this.value));
        writer.startTAG("AvailableType").textNode(String.valueOf(this.availableType));
        writer.startTAG("Title").textNode(this.title);
        writer.startTAG("Description").textNode(this.description);
        writer.startTAG("RelationKey").textNode(this.relationKey);
        writer.end();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
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

    public AvailableType getAvailableType() {
        return availableType;
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

    public void setPropertyKey(String propertyKey) {
        this.propertyKey = propertyKey;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    public void setAvailableType(AvailableType availableType) {
        this.availableType = availableType;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRelationKey(String relationKey) {
        this.relationKey = relationKey;
    }

    public List<PropertiesConfig> getPropertyList () {
        return this.propertyList;
    }

    public Modification getIsModified() {
        return isModified;
    }

    public boolean checkNull () {
        if (StringUtils.isNotEmpty(this.propertyKey) && StringUtils.isNotEmpty(this.propertyName) && this.valueType != null && this.value != null && this.availableType != null && StringUtils.isNotEmpty(this.title) && StringUtils.isNotEmpty(this.description)) {
            return true;
        }
        return false;
    }
}
