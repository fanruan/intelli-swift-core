package com.finebi.base.data.xml.process.impl;


import com.finebi.base.data.xml.item.XmlItem;
import com.finebi.base.data.xml.node.XmlNode;
import com.finebi.base.data.xml.process.XmlNodeProcess;
import com.finebi.base.data.xml.process.XmlStructureNodeFactory;
import com.finebi.base.environmen.Environment;
import com.finebi.base.utils.annotations.AnnotationsUtils;
import com.finebi.base.utils.data.map.MapUtils;
import com.finebi.base.utils.data.xml.XmlItemUtils;
import com.finebi.base.utils.data.xml.imp.node.XmlNodeUtils;
import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.sax.TransformerHandler;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * @author andrew.asa
 * @create 2017-10-21
 **/
public class XmlBaseNodeProcess extends AbstractXmlNodeProcess {

    BILogger LOGGER = BILoggerFactory.getLogger(XmlBaseNodeProcess.class);

    @Override
    public void createXmlNodeByClass(Class clazz, XmlNode root) throws Exception {

        Map<Field, XmlItem> items = AnnotationsUtils.getAnnotationFields(clazz, XmlItem.class, true);
        if (MapUtils.isEmptyMap(items)) {
            return;
        }
        for (Field f : items.keySet()) {
            XmlItem item = items.get(f);
            Class c = f.getType();
            XmlNode child;
            if (XmlItemUtils.isBaseXmlElement(c)) {
                child = new XmlNode(c, f.getName());
            } else {
                child = XmlNodeUtils.getXmlNodeByClass(c);
                // 接口类型的处理
                if (item.isInterface()) {
                    child.setInterface(true);
                    child.setValueFactory(item.objectFactory());
                }
            }
            root.addChild(child);
        }
    }

    @Override
    public boolean shouldDealWith(XmlNode node) {

        return node.getClass().equals(XmlNode.class);
    }

    @Override
    public String getName() {

        return "XmlBaseNodeProcess";
    }

    @Override
    public Object startElement(String uri, String localName, String qName, Attributes attributes, XmlNode node, Stack stack) throws Exception {

        // 不是基本数据类型
        Object o = null;
        if (!XmlItemUtils.isBaseXmlElement(node.getClazz())) {
            if (node.isInterface()) {
                // 接口类型处理 需要用具体的构造工厂类以及构造函数
            } else {
                try {
                    o = node.getClazz().newInstance();
                } catch (Exception e) {
                    LOGGER.info("error in new instance " + o.getClass().toString(), e);
                }
            }
            stack.push(o);
            return o;
        }
        return o;
    }

    @Override
    public void characters(char[] ch, int start, int length, XmlNode node, Stack stack) {

        if (XmlItemUtils.isBaseXmlElement(node.getClazz())) {
            // 基本数据类型
            String data = new String(ch, start, length);
            Object container = stack.peek();
            try {
                AnnotationsUtils.invokeSetMethodByFieldName(container, node.getTagName(), data, node.getClazz());
            } catch (Exception e) {
                LOGGER.info("error in set field " + node.getTagName(), e);
            }
        }
    }

    @Override
    public Object endElement(Environment environment) {

        XmlNode node = environment.getElement(3);
        Stack stack = environment.getElement(4);
        if (!XmlItemUtils.isBaseXmlElement(node.getClazz())) {
            return stack.pop();
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
        Class clazz = node.getClazz();
        try {
            createInterfaceAttr(o, node, attr);
            handler.startElement("", "", tagName, attr);
            // 如果是基本数据类型
            if (XmlItemUtils.isBaseXmlElement(clazz)) {
                // todo 如果是boolean类型的，并不一定是getxxx也可能是isxxx而已
                Object subTag = AnnotationsUtils.invokeGetMethodByFieldName(o, tagName);
                XmlItemUtils.writeXml(handler, subTag);
            } else {
                List<XmlNode> childs = node.getChilds();
                // 递归写入儿子结点
                for (XmlNode child : childs) {
                    XmlNodeProcess process = XmlStructureNodeFactory.getProcessByXmlNode(child);
                    process.writeNode(o, child, handler, attr);
                }
            }
        } finally {
            handler.endElement("", "", tagName);
        }
    }

    public void createInterfaceAttr(Object o, XmlNode node, AttributesImpl attributes) {
        // 如果是接口类型
        if (node.isInterface()) {

        }
    }
}
