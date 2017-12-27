package com.finebi.base.data.xml.node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrew_asa on 2017/10/9.
 * 基本xml node结构
 */
public class XmlNode {


    Class clazz;

    String tagName;

    Class valueFactory;

    XmlNode parent;

    boolean isInterface = false;

    List<XmlNode> childs = new ArrayList<XmlNode>();

    public XmlNode() {

    }

    public XmlNode(Class clazz, String tagName) {

        this.clazz = clazz;
        this.tagName = tagName;
    }

    public XmlNode(Object o, String tagName) {

        this(o.getClass(), tagName);
    }


    public <T extends XmlNode> void addChild(T child) {

        child.setParent(this);
        childs.add(child);
    }

    public void addAll(List<XmlNode> childs) {

        this.childs.addAll(childs);
    }

    public <T extends XmlNode> T getChild(int i) {

        return (T) childs.get(i);
    }

    public void clearChild() {

        childs.clear();
    }

    public void setClazz(Class clazz) {

        this.clazz = clazz;
    }

    public void setTagName(String tagName) {

        this.tagName = tagName;
    }


    public void setInterface(boolean anInterface) {

        isInterface = anInterface;
    }

    public void setChilds(List<XmlNode> childs) {

        this.childs = childs;
    }

    public void setValueFactory(Class valueFactory) {

        this.valueFactory = valueFactory;
    }

    public void setParent(XmlNode parent) {

        this.parent = parent;
    }

    public Class getClazz() {

        return clazz;
    }

    public String getTagName() {

        return tagName;
    }

    public List<XmlNode> getChilds() {

        return childs;
    }

    public boolean isInterface() {

        return isInterface;
    }

    public Class getValueFactory() {

        return valueFactory;
    }

    public <T extends XmlNode> T getParent() {

        if (parent == null) {
            return null;
        }
        return (T) parent;
    }
}
