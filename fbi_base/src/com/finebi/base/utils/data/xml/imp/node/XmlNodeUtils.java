package com.finebi.base.utils.data.xml.imp.node;


import com.finebi.base.data.xml.node.XmlNode;
import com.finebi.base.data.xml.node.XmlObjectNode;
import com.finebi.base.data.xml.process.XmlNodeProcess;
import com.finebi.base.data.xml.process.XmlStructureNodeFactory;
import com.finebi.base.utils.data.list.ListUtils;
import com.finebi.base.utils.data.xml.XmlItemUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by andrew_asa on 2017/10/11.
 * 处理node结构的工具类
 */
public class XmlNodeUtils {

    public static final XmlNode BASENODE = new XmlNode(-99, "---BASE---");

    /**
     * 根据class获取xml node结构
     * 需要持久化的xml对象
     *
     * @param clazz
     * @return
     * @throws Exception
     */
    public static XmlNode getXmlNodeByClass(Class clazz) throws Exception {

        if (XmlItemUtils.isBaseXmlElement(clazz)) {
            return XmlNodeUtils.BASENODE;
        }
        XmlNode root = new XmlNode(clazz, clazz.getSimpleName());
        Map<String, XmlNodeProcess> processMap = XmlStructureNodeFactory.getProcess();
        for (String k : processMap.keySet()) {
            processMap.get(k).createXmlNodeByClass(clazz, root);
        }
        return root;
    }

    public static XmlObjectNode getXmlObjectNodeByClass(Class clazz) throws Exception {

        XmlObjectNode root = new XmlObjectNode(clazz, clazz.getSimpleName());
        Map<String, XmlNodeProcess> processMap = XmlStructureNodeFactory.getProcess();
        for (String k : processMap.keySet()) {
            processMap.get(k).createXmlNodeByClass(clazz, root);
        }
        return root;

    }

    public static XmlNode getNodeByChain(XmlNode root, List<String> chain) {

        if (ListUtils.isEmptyList(chain)) {
            return root;
        }
        XmlNode parent = root;
        XmlNode child = root;
        for (int i = 0; i < chain.size(); i++) {
            for (XmlNode n : parent.getChilds()) {
                String tagName = chain.get(i);
                if (n.getTagName().equals(tagName)) {
                    child = n;
                    break;
                }
            }
            parent = child;
        }
        return parent;
    }

}
