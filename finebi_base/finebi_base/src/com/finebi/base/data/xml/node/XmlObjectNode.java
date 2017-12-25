package com.finebi.base.data.xml.node;

/**
 * Created by andrew_asa on 2017/10/23.
 */
public class XmlObjectNode extends XmlNode{

    public XmlObjectNode() {

    }

    public XmlObjectNode(Class clazz, String tagName) {
        super(clazz,tagName);
    }
}
