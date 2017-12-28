package com.finebi.base.data.xml.process.impl;


import com.finebi.base.data.xml.XmlObjectFactory;
import com.finebi.base.data.xml.item.XmlObjectItem;
import com.finebi.base.data.xml.node.XmlNode;
import com.finebi.base.data.xml.node.XmlObjectNode;
import com.finebi.base.data.xml.process.XmlNodeProcess;
import com.finebi.base.data.xml.process.XmlStructureNodeFactory;
import com.finebi.base.environmen.Environment;
import com.finebi.base.utils.annotations.AnnotationsUtils;
import com.finebi.base.utils.data.map.MapUtils;
import com.finebi.base.utils.data.xml.XmlItemUtils;
import com.finebi.base.utils.data.xml.imp.node.XmlNodeUtils;
import com.fr.stable.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.sax.TransformerHandler;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Created by andrew_asa on 2017/10/23.
 */
public class XmlObjectNodeProcess extends AbstractXmlNodeProcess {

    @Override
    public void createXmlNodeByClass(Class clazz, XmlNode root) throws Exception {
        Map<Field, XmlObjectItem> items = AnnotationsUtils.getAnnotationFields(clazz, XmlObjectItem.class, true);
        if (MapUtils.isEmptyMap(items)) {
            return;
        }
        for (Field f : items.keySet()) {
            XmlObjectItem item = items.get(f);
            Class c = f.getType();
            XmlNode child;
            if (XmlItemUtils.isBaseXmlElement(c)) {
                child = new XmlNode(c, f.getName());
            } else {
                child = XmlNodeUtils.getXmlNodeByClass(c);
                // 接口类型的处理
                if (item.isInterface()) {
                    child.setInterface(true);
                    child.setValueFactory(item.constructObject());
                }
            }
            root.addChild(child);
        }
    }

    @Override
    public boolean shouldDealWith(XmlNode node) {

        return node.getClass().equals(XmlObjectNode.class);
    }

    @Override
    public String getName() {

        return "XmlObjectNodeProcess";
    }

    @Override
    public Object startElement(String uri, String localName, String qName, Attributes attributes, XmlNode node, Stack stack) throws Exception {

        XmlObjectNode objectNode = translateNode(node);
        Object o;
        if (objectNode.isInterface()) {
            // 如果是接口类型
            Class factoryClazz = objectNode.getValueFactory();

            XmlObjectFactory factory = getValueFactory(factoryClazz);
            o = factory.getObject(attributes);
            XmlNode cn = XmlNodeUtils.getXmlObjectNodeByClass(o.getClass());
            node.addAll(cn.getChilds());
        } else {
            o = objectNode.getClazz().newInstance();
        }
        if (o != null) {
            stack.push(o);
        }
        return o;
    }

    @Override
    public void characters(char[] ch, int start, int length, XmlNode node, Stack stack) {

    }

    @Override
    public Object endElement(Environment environment) {

        XmlNode node = environment.getElement(3);
        if (node.isInterface()) {
            node.clearChild();
        }
        return null;
    }

    @Override
    public void afterEndElement(Environment environment) {

    }

    @Override
    public void writeNode(Object o, XmlNode node, TransformerHandler handler, AttributesImpl attr) throws Exception {

        attr.clear();
        String tagName = node.getTagName();
        XmlObjectNode objectNode = translateNode(node);
        createAttr(o, objectNode, attr);
        handler.startElement(StringUtils.EMPTY, StringUtils.EMPTY, tagName, attr);
        try {
            if (objectNode.isInterface()) {
                // 如果是接口类型需要重新获取对象的node
                XmlNode on = getInterfaceNode(o);
                List<XmlNode> childs = on.getChilds();
                for (XmlNode child : childs) {
                    XmlNodeProcess process = XmlStructureNodeFactory.getProcessByXmlNode(child);
                    process.writeNode(o, child, handler, attr);
                }
            }
        } finally {
            handler.endElement(StringUtils.EMPTY, StringUtils.EMPTY, tagName);
        }
    }

    /**
     * 生成标签属性
     *
     * @param o
     * @param node
     * @param attr
     */
    private void createAttr(Object o, XmlObjectNode node, AttributesImpl attr) throws Exception {

        // 只有接口的时候才有这个必要
        if (node.isInterface()) {
            XmlObjectFactory factory = getValueFactory(node.getValueFactory());
            factory.createAttr(o, attr);
        }
    }

}
