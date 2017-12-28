package com.finebi.base.data.xml.process.impl;


import com.finebi.base.data.xml.item.XmlMapItem;
import com.finebi.base.data.xml.node.XmlMapNode;
import com.finebi.base.data.xml.node.XmlNode;
import com.finebi.base.data.xml.node.XmlObjectNode;
import com.finebi.base.data.xml.process.XmlNodeProcess;
import com.finebi.base.data.xml.process.XmlStructureNodeFactory;
import com.finebi.base.environmen.Environment;
import com.finebi.base.utils.annotations.AnnotationsUtils;
import com.finebi.base.utils.data.clazz.ClassUtils;
import com.finebi.base.utils.data.list.ListUtils;
import com.finebi.base.utils.data.map.MapUtils;
import com.finebi.base.utils.data.xml.XmlItemUtils;
import com.finebi.base.utils.data.xml.imp.node.XmlNodeUtils;
import com.finebi.log.BILogger;
import com.finebi.log.BILoggerFactory;
import com.fr.stable.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.sax.TransformerHandler;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * @author andrew.asa
 * @create 2017-10-21
 **/
public class XmlMapNodeProcess extends AbstractXmlNodeProcess {

    BILogger LOGGER = BILoggerFactory.getLogger(XmlMapNodeProcess.class);

    public static final String KEYNAMETAG = "key";

    @Override
    public void createXmlNodeByClass(Class clazz, XmlNode root) throws Exception {

        Map<Field, XmlMapItem> items = AnnotationsUtils.getAnnotationFields(clazz, XmlMapItem.class, true);
        if (MapUtils.isEmptyMap(items)) {
            return;
        }
        for (Field f : items.keySet()) {
            XmlMapItem item = items.get(f);
            Class kc = item.keyClass();
            Class vc = item.valueClass();
            Class vfc = item.valueFactory();
            XmlMapNode c = new XmlMapNode();
            c.setTagName(f.getName());
            c.setKeyClass(kc);
            // value class
            c.setValueClass(vc);
            if (!XmlItemUtils.isBaseXmlElement(vc)) {
                XmlNode cn;
                // 说明值为接口类型
                if (!String.class.equals(vfc)) {
                    c.setValueIsInterface(true);
                    c.setValueFactory(vfc);
                    cn = new XmlObjectNode();
                    cn.setInterface(true);
                    cn.setValueFactory(vfc);
                    cn.setTagName(vc.getSimpleName());
                    // 不需要扫描了，生成和读取的时候的时候才进行动态扫描
                } else {
                    // 非接口的对象类型 -- 仍然需要进一步扫描
                    cn = XmlNodeUtils.getXmlObjectNodeByClass(vc);
                }
                c.addChild(cn);
                // 设置键
                XmlMapNode mapKeyNode = new XmlMapNode();
                mapKeyNode.setIsKey(true);
                mapKeyNode.setTagName(KEYNAMETAG);
                c.addChild(mapKeyNode);
                mapKeyNode.addChild(cn);
            }
            root.addChild(c);
        }
    }

    @Override
    public boolean shouldDealWith(XmlNode node) {

        return node.getClass().equals(XmlMapNode.class);
    }

    @Override
    public String getName() {

        return "XmlMapNodeProcess";
    }

    @Override
    public Object startElement(String uri, String localName, String qName, Attributes attributes, XmlNode node, Stack stack) {

        XmlMapNode mapNode = translateNode(node);
        // 如果是键值
        if (mapNode.isKey()) {
            String key = attributes.getValue(KEYNAMETAG);
            stack.push(key);
        }
        return null;
    }

    @Override
    public void characters(char[] ch, int start, int length, XmlNode node, Stack stack) {

    }

    @Override
    public Object endElement(Environment environment) {

        XmlMapNode mapNode = environment.getElement(3);
        Stack stack = environment.getElement(4);
        // 如果是键值
        if (mapNode.isKey()) {
            Object value = stack.pop();
            Object key = stack.pop();
            Object container = stack.peek();
            XmlMapNode parentNode = mapNode.getParent();
            String mapFieldName = parentNode.getTagName();
            try {
                AnnotationsUtils.invokeMethodByType(container, "add" + AnnotationsUtils.upperFirstChart(mapFieldName), ClassUtils.toArray(parentNode.getKeyClass(), parentNode.getValueClass()), ListUtils.toArray(key, value));
            } catch (Exception e) {
                LOGGER.info("error in invoke add Method " + mapFieldName, e);
            }
        }
        return null;
    }

    @Override
    public void afterEndElement(Environment environment) {

    }

    @Override
    public void writeNode(Object o, XmlNode node, TransformerHandler handler, AttributesImpl attr) throws Exception {

        String tagName = node.getTagName();
        attr.clear();
        handler.startElement(StringUtils.EMPTY, StringUtils.EMPTY, tagName, attr);
        try {
            XmlMapNode mapNode = translateNode(node);
            Map<Object, Object> map = AnnotationsUtils.invokeGetMethod(o, mapNode.getTagName());
            // value 是否为接口类型
            boolean isValueInterface = mapNode.isValueIsInterface();
            // value 是否为基本数据类型
            boolean isValueBaseElement = !isValueInterface && XmlItemUtils.isBaseXmlElement(mapNode.getValueClass());
            if (MapUtils.isNotEmptyMap(map)) {
                Set<Object> keys = map.keySet();
                for (Object key : keys) {
                    attr.clear();
                    attr.addAttribute(StringUtils.EMPTY, StringUtils.EMPTY, KEYNAMETAG, StringUtils.EMPTY, key.toString());
                    handler.startElement(StringUtils.EMPTY, StringUtils.EMPTY, KEYNAMETAG, attr);
                    Object value = map.get(key);
                    if (isValueBaseElement) {
                        // 如果value是基本数据类型
                    } else {
                        //XmlNode cn = getMapValueXmlNode(value, mapNode, attr);
                        XmlNode cn = mapNode.getChild(0);
                        XmlNodeProcess process = XmlStructureNodeFactory.getProcessByXmlNode(cn);
                        process.writeNode(value, cn, handler, attr);
                    }
                    handler.endElement(StringUtils.EMPTY, StringUtils.EMPTY, KEYNAMETAG);
                }
            }
        } finally {
            handler.endElement(StringUtils.EMPTY, StringUtils.EMPTY, tagName);
        }
    }
}
