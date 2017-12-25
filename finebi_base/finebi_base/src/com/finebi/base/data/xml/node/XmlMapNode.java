package com.finebi.base.data.xml.node;

/**
 * Created by andrew_asa on 2017/10/11.
 * map结构
 */
public class XmlMapNode extends XmlNode {

    Class valueClass;

    Class keyClass;

    boolean keyIsInterface = false;

    boolean valueIsInterface = false;

    /**
     * 是否为键对象
     */
    boolean isKey;

    /**
     * 是否为值对象
     */
    boolean isValue;

    /**
     * 键工厂类
     */
    Class keyFactory;

    /**
     * 值工厂类
     */
    Class valueFactory;

    public XmlMapNode() {

    }

    public XmlMapNode(Class clazz, String tagName) {

        super(clazz, tagName);
    }

    public void setValueClass(Class valueClass) {

        this.valueClass = valueClass;
    }

    public void setKeyClass(Class keyClass) {

        this.keyClass = keyClass;
    }

    public void setKeyIsInterface(boolean keyIsInterface) {

        this.keyIsInterface = keyIsInterface;
    }

    public void setValueIsInterface(boolean valueIsInterface) {

        this.valueIsInterface = valueIsInterface;
    }

    public void setKeyFactory(Class keyFactory) {

        this.keyFactory = keyFactory;
    }

    public void setValueFactory(Class valueFactory) {

        this.valueFactory = valueFactory;
    }

    public Class getValueClass() {

        return valueClass;
    }

    public Class getKeyClass() {

        return keyClass;
    }

    public boolean isKeyIsInterface() {

        return keyIsInterface;
    }

    public boolean isValueIsInterface() {

        return valueIsInterface;
    }

    public Class getKeyFactory() {

        return keyFactory;
    }

    public Class getValueFactory() {

        return valueFactory;
    }

    public boolean isKey() {

        return isKey;
    }

    public boolean isValue() {

        return isValue;
    }

    public void setIsKey(boolean key) {

        isKey = key;
    }

    public void setIsValue(boolean value) {

        isValue = value;
    }
}
