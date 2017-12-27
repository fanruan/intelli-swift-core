package com.finebi.base.data.xml.process.impl;

import com.finebi.base.data.xml.XmlObjectFactory;
import com.finebi.base.data.xml.node.XmlNode;
import com.finebi.base.data.xml.process.XmlNodeProcess;
import com.finebi.base.utils.data.xml.imp.node.XmlNodeUtils;

/**
 * @author andrew.asa
 * @create 2017-10-21
 **/
public abstract class AbstractXmlNodeProcess implements XmlNodeProcess {

    public <T> T translateNode(XmlNode node) {

        return (T) node;
    }

    public XmlObjectFactory getValueFactory(Class factoryClass) throws Exception {

        return (XmlObjectFactory) factoryClass.newInstance();
    }

    public XmlNode getInterfaceNode(Object o) throws Exception{

        return XmlNodeUtils.getXmlNodeByClass(o.getClass());
    }
}
