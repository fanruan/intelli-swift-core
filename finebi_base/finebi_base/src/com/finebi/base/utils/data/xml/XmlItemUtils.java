package com.finebi.base.utils.data.xml;


import com.finebi.base.constant.XMLConstant;
import com.finebi.base.data.xml.node.XmlNode;
import com.finebi.base.utils.data.characters.CharUtils;
import com.fr.stable.StringUtils;

import javax.xml.transform.sax.TransformerHandler;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by andrew_asa on 2017/10/9.
 */
public class XmlItemUtils {

    //    public static final XmlNode EMPTYNODE = new XmlNode(-99, "---EMPTY---");
    //
    //
    //
    //    public static XmlNode getEmptynode() {
    //
    //        return EMPTYNODE;
    //    }
    //
    //    public static boolean isEmptyNode(XmlNode node) {
    //
    //        return EMPTYNODE.equals(node);
    //    }
    //
    //    public static XmlNode createListNode() {
    //
    //        return new XmlNode(XMLConstant.XMLITEMTYPE.LIST);
    //    }
    //
    //    public static XmlNode createListNode(String tagName) {
    //
    //        return new XmlNode(XMLConstant.XMLITEMTYPE.LIST, tagName);
    //    }
    //
    //    public static XmlNode createMapNode() {
    //
    //        return new XmlNode(XMLConstant.XMLITEMTYPE.MAP);
    //    }


    public static int classToXmlItemType(Class clazz) {

        if (Double.class.equals(clazz) || double.class.equals(clazz)) {
            return XMLConstant.XMLITEMTYPE.DOUBLE;
        } else if (Long.class.equals(clazz) || long.class.equals(clazz)) {
            return XMLConstant.XMLITEMTYPE.LONG;
        } else if (Integer.class.equals(clazz) || int.class.equals(clazz)) {
            return XMLConstant.XMLITEMTYPE.INT;
        } else if (Boolean.class.equals(clazz) || boolean.class.equals(clazz)) {
            return XMLConstant.XMLITEMTYPE.BOOLEAN;
        } else if (List.class.equals(clazz)) {
            return XMLConstant.XMLITEMTYPE.LIST;
        } else if (Map.class.equals(clazz)) {
            return XMLConstant.XMLITEMTYPE.MAP;
        } else if (Date.class.equals(clazz)) {
            return XMLConstant.XMLITEMTYPE.DATE;
        } else if (String.class.equals(clazz)) {
            return XMLConstant.XMLITEMTYPE.STRING;
        }
        return XMLConstant.XMLITEMTYPE.OBJECT;
    }

    public static boolean isBaseXmlElement(int type) {

        return type == XMLConstant.XMLITEMTYPE.INT ||
                type == XMLConstant.XMLITEMTYPE.LONG ||
                type == XMLConstant.XMLITEMTYPE.DOUBLE ||
                type == XMLConstant.XMLITEMTYPE.BOOLEAN ||
                type == XMLConstant.XMLITEMTYPE.STRING;
    }

    //    public static boolean isBaseElement(XmlNode node) {
    //
    //        return isBaseXmlElement(node.getType());
    //    }
    //
    //    public static boolean isListElement(XmlNode node) {
    //
    //        return isXmlListElement(node.getType());
    //    }
    //
    //    public static boolean isMapElement(XmlNode node) {
    //
    //        return isXmlMapElement(node.getType());
    //    }

    public static boolean isBaseXmlElement(Class clazz) {

        return Boolean.class.equals(clazz) ||
                Double.class.equals(clazz) ||
                Long.class.equals(clazz) ||
                String.class.equals(clazz) ||
                Integer.class.equals(clazz) ||
                boolean.class.equals(clazz) ||
                double.class.equals(clazz) ||
                long.class.equals(clazz) ||
                int.class.equals(clazz);
    }

    public static boolean isBaseObjectXmlElement(Class clazz) {

        return Boolean.class.equals(clazz) ||
                Double.class.equals(clazz) ||
                Long.class.equals(clazz) ||
                String.class.equals(clazz) ||
                Integer.class.equals(clazz);
    }

    public static boolean isXmlListElement(int type) {

        return type == XMLConstant.XMLITEMTYPE.LIST;
    }

    public static boolean isXmlMapElement(int type) {

        return type == XMLConstant.XMLITEMTYPE.MAP;
    }

    public static boolean isXmlDateElement(int type) {

        return type == XMLConstant.XMLITEMTYPE.DATE;
    }

    public static boolean isXmlObjectElement(int type) {

        return type == XMLConstant.XMLITEMTYPE.OBJECT;
    }

    //    public static XmlNode getXmlNodeByClass(Class clazz) throws Exception {
    //
    //        Map<Field, XmlItem> map = AnnotationsUtils.getAnnotationFields(clazz, XmlItem.class);
    //        if (MapUtils.isEmptyMap(map)) {
    //            return XmlItemUtils.EMPTYNODE;
    //        }
    //        XmlNode root = new XmlNode(clazz, clazz.getSimpleName());
    //        XmlNode child;
    //        for (Field field : map.keySet()) {
    //            XmlItem item = map.get(field);
    //            if (XmlItemUtils.isListItem(item)) {
    //                child = parseListField(field, item.listElementType());
    //            } else if (XmlItemUtils.isMapItem(item)) {
    //                // map类型
    //                child = parseMapField(field, item);
    //            } else {
    //                // 基本数据类型
    //                child = parseBaseField(field);
    //                child.setTagName(field.getName());
    //            }
    //            // todo map 类型的还需要进一步支持
    //            root.addChild(child);
    //        }
    //        return root;
    //    }

    private static XmlNode parseBaseField(Field field) throws Exception {

        Class clazz = field.getType();
        return new XmlNode(clazz, field.getName());
    }

    //    private static XmlNode parseListField(Field field, Class listElement) throws Exception {
    //
    //        XmlNode root = new XmlNode(List.class, field.getName());
    //        // list里面是基本数据类型
    //        if (XmlItemUtils.isBaseObjectXmlElement(listElement)) {
    //            root.setBaseListClass(listElement);
    //        } else {
    //            XmlNode c = getXmlNodeByClass(listElement);
    //            root.addChild(c);
    //        }
    //        return root;
    //    }

    //    private static XmlNode parseMapField(Field field, XmlItem item) throws Exception {
    //
    //        XmlNode root = new XmlNode(Map.class, field.getName());
    //        // 如果是自动获取class类型，那么久
    //        if (!item.autoLoadMapClass()) {
    //            Class clazz = item.listElementType();
    //            // 基本数据结构类型
    //            if (XmlItemUtils.isBaseObjectXmlElement(clazz)) {
    //
    //            } else {
    //                root.addChild(getXmlNodeByClass(item.mapValueElementType()));
    //            }
    //        }
    //        return root;
    //    }

    public static String baseXmlTypeValueToString(int type, Object o) {

        return o.toString();
    }

    public static void writeXml(TransformerHandler handler, Object data) throws Exception {

        String str;
        if (data != null) {
            str = data.toString();
        } else {
            str = StringUtils.EMPTY;
        }
        char[] tc = CharUtils.stringToCharacters(str);
        handler.characters(tc, 0, tc.length);
    }
}
