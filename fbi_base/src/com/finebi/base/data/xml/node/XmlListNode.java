package com.finebi.base.data.xml.node;

/**
 * Created by andrew_asa on 2017/10/11.
 * list结构
 */
public class XmlListNode extends XmlNode {

    Class baseClass;

    public XmlListNode() {

    }

    public XmlListNode(Class clazz, String tagName) {

        super(clazz, tagName);
    }

    public XmlListNode(Object o, String tagName) {

        super(o, tagName);
    }


    public boolean isBaseElement() {

        return baseClass != null;
    }

    public void setBaseClass(Class baseClass) {

        this.baseClass = baseClass;
    }

    public Class getBaseClass() {

        return baseClass;
    }
}
