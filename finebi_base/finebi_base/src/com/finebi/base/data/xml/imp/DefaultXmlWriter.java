package com.finebi.base.data.xml.imp;


import com.finebi.base.data.xml.node.XmlNode;
import com.finebi.base.data.xml.process.XmlNodeProcess;
import com.finebi.base.data.xml.process.XmlStructureNodeFactory;
import com.finebi.base.utils.data.xml.imp.node.XmlNodeUtils;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

/**
 * Created by andrew_asa on 2017/9/30.
 */
public class DefaultXmlWriter {

    public DefaultXmlWriter() {

    }

    public void write(File dst, Object obj) throws Exception {

        SAXTransformerFactory factory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
        TransformerHandler handler = factory.newTransformerHandler();
        Transformer transformer = handler.getTransformer();
        // 是否自动添加额外的空白
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        // 设置字符编码
        transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
        transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
        StreamResult result = new StreamResult(dst);
        handler.setResult(result);
        // 开始xml
        handler.startDocument();
        AttributesImpl attributes = new AttributesImpl();
        XmlNode node = XmlNodeUtils.getXmlNodeByClass(obj.getClass());
        writeNode(obj, node, handler, attributes);
        handler.endDocument();
    }

    private void writeNode(Object o, XmlNode node, TransformerHandler handler, AttributesImpl attr) throws Exception {

        XmlNodeProcess process = XmlStructureNodeFactory.getProcessByXmlNode(node);
        process.writeNode(o,node,handler,attr);

//        attr.clear();
//        String tagName = node.getTagName();
//        //char[] tc = CharUtils.stringToCharacters(tagName);
//        handler.startElement(StringUtils.EMPTY, StringUtils.EMPTY, tagName, attr);
//        int type = node.getType();
//        try {
//            // 基本数据类型 -- 直接写入
//            if (XmlItemUtils.isBaseXmlElement(type)) {
//                Object subTag = AnnotationsUtils.invokeGetMethod(o, tagName);
//                XmlItemUtils.writeXml(handler, subTag);
//            }
//            // 对象 -- 遍历属性
//            if (XmlItemUtils.isXmlObjectElement(type)) {
//                List<XmlNode> childNodes = node.getChilds();
//                if (ListUtils.isNotEmptyList(childNodes)) {
//                    for (XmlNode childNode : childNodes) {
//                        writeNode(o, childNode, handler, attr);
//                    }
//                }
//            }
//            // list -- 遍历子元素
//            if (XmlItemUtils.isXmlListElement(type)) {
//                List<Object> childrens = (List<Object>) AnnotationsUtils.invokeGetMethod(o, tagName);
//                // 非基本数据类型
//                if (node.getBaseListClass() != null) {
//                    // list里面是数据基本类型
//                    JSONArray array = JsonUtils.listToJsonArray(childrens);
//                    // 需要用cdata包裹起来防止不必要的转义
//                    handler.startCDATA();
//                    XmlItemUtils.writeXml(handler, array);
//                    handler.endCDATA();
//                } else {
//                    if (ListUtils.isNotEmptyList(childrens)) {
//                        XmlNode childNode = ListUtils.getFirstElement(node.getChild());
//                        for (Object child : childrens) {
//                            writeNode(child, childNode, handler, attr);
//                        }
//                    }
//                }
//            }
//            // map -- TODO
//            if (XmlItemUtils.isXmlMapElement(type)) {
//
//            }
//        } catch (Exception e) {
//
//        } finally {
//            // 确保闭合
//            handler.endElement(StringUtils.EMPTY, StringUtils.EMPTY, tagName);
//        }
    }
}
