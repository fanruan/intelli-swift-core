package com.finebi.base.data.xml.process.impl;


import com.finebi.base.data.xml.item.XmlListItem;
import com.finebi.base.data.xml.node.XmlListNode;
import com.finebi.base.data.xml.node.XmlNode;
import com.finebi.base.data.xml.process.XmlNodeProcess;
import com.finebi.base.data.xml.process.XmlStructureNodeFactory;
import com.finebi.base.environmen.Environment;
import com.finebi.base.utils.annotations.AnnotationsUtils;
import com.finebi.base.utils.data.clazz.ClassUtils;
import com.finebi.base.utils.data.json.JsonUtils;
import com.finebi.base.utils.data.list.ListUtils;
import com.finebi.base.utils.data.map.MapUtils;
import com.finebi.base.utils.data.xml.XmlItemUtils;
import com.finebi.base.utils.data.xml.imp.node.XmlNodeUtils;
import com.finebi.log.BILogger;
import com.finebi.log.BILoggerFactory;
import com.fr.json.JSONArray;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.sax.TransformerHandler;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * @author andrew.asa
 * @create 2017-10-21
 **/
public class XmlListNodeProcess extends AbstractXmlNodeProcess {

    BILogger LOGGER = BILoggerFactory.getLogger(XmlListNodeProcess.class);

    @Override
    public void createXmlNodeByClass(Class clazz, XmlNode root) throws Exception {

        Map<Field, XmlListItem> items = AnnotationsUtils.getAnnotationFields(clazz, XmlListItem.class, true);
        if (MapUtils.isEmptyMap(items)) {
            return;
        }
        for (Field f : items.keySet()) {
            XmlListItem item = items.get(f);
            Class c = item.clazz();
            String tagName = f.getName();
            XmlListNode listRoot = new XmlListNode(c, tagName);
            root.addChild(listRoot);
            if (!XmlItemUtils.isBaseXmlElement(c)) {
                XmlNode child = XmlNodeUtils.getXmlNodeByClass(c);
                // 接口类型处理
                if (item.isInterface()) {
                    listRoot.setInterface(true);
                    listRoot.setValueFactory(item.constructObject());
                }
                listRoot.addChild(child);
            }
        }
    }

    @Override
    public boolean shouldDealWith(XmlNode node) {

        return node.getClass().equals(XmlListNode.class);
    }

    @Override
    public String getName() {

        return "XmlListNodeProcess";
    }

    @Override
    public Object startElement(String uri, String localName, String qName, Attributes attributes, XmlNode node, Stack stack) {

        return null;
    }

    @Override
    public void characters(char[] ch, int start, int length, XmlNode node, Stack stack) {

        Class cls = node.getClazz();
        // 基本数据类型
        if (XmlItemUtils.isBaseXmlElement(cls)) {
            Object container = stack.peek();
            String ldata = new String(ch, start, length);
            List<Object> list = new ArrayList<Object>();
            String methodName = null;
            try {
                JSONArray json = new JSONArray(JsonUtils.makeSureNotNullArrayStr(ldata));
                for (int i = 0; i < json.length(); i++) {
                    Object item = AnnotationsUtils.getBaseObjectFromStr(cls, json.getString(i));
                    list.add(item);
                }
                methodName = AnnotationsUtils.getSetMethodName(node.getTagName());
                AnnotationsUtils.invokeMethodByType(container, methodName, ClassUtils.toArray(List.class), ListUtils.toArray(list));
            } catch (Exception e) {
                LOGGER.info("error invoke method " + methodName, e);
            }
        } else {

        }
    }

    @Override
    public Object endElement(Environment environment) {

        return null;
    }

    @Override
    public void afterEndElement(Environment environment) {

        try {
            Stack stack = environment.getElement(0);
            String qName = environment.getElement(1);
            Object last = environment.getElement(2);
            Object o = stack.peek();
            // 父是数据队列
            AnnotationsUtils.invokeMethod(o, getAddMethodName(qName), last);
        } catch (Exception e) {
            LOGGER.info("error in set list element ", e);
        }
    }

    private String getAddMethodName(String tagName) {

        return "add" + tagName;
    }

    @Override
    public void writeNode(Object o, XmlNode node, TransformerHandler handler, AttributesImpl attr) throws Exception {

        attr.clear();
        String tagName = node.getTagName();
        Class clazz = node.getClazz();
        attr.clear();
        try {
            XmlListNode ln = translateNode(node);
            handler.startElement("", "", tagName, attr);
            List<Object> list = AnnotationsUtils.invokeGetMethod(o, ln.getTagName());
            // 基本数据类型
            if (XmlItemUtils.isBaseXmlElement(clazz)) {
                JSONArray array = JsonUtils.listToJsonArray(list);
                // 需要用cdata包裹起来防止不必要的转义
                handler.startCDATA();
                XmlItemUtils.writeXml(handler, array);
                handler.endCDATA();
            } else {
                XmlNode cn = node.getChild(0);
                XmlNodeProcess process = XmlStructureNodeFactory.getProcessByXmlNode(cn);
                if (ListUtils.isNotEmptyList(list)) {
                    for (Object lo : list) {
                        process.writeNode(lo, cn, handler, attr);
                    }
                }
            }
        } finally {
            handler.endElement("", "", tagName);
        }
    }
}
