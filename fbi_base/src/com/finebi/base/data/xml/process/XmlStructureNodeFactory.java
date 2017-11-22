package com.finebi.base.data.xml.process;


import com.finebi.base.data.xml.node.XmlNode;
import com.finebi.base.data.xml.process.impl.DefaultXmlNodeProcess;
import com.finebi.base.data.xml.process.impl.XmlBaseNodeProcess;
import com.finebi.base.data.xml.process.impl.XmlListNodeProcess;
import com.finebi.base.data.xml.process.impl.XmlMapNodeProcess;
import com.finebi.base.data.xml.process.impl.XmlObjectNodeProcess;
import com.finebi.base.utils.data.xml.imp.node.XmlNodeUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * xml
 *
 * @author andrew.asa
 * @create 2017-10-21
 **/
public class XmlStructureNodeFactory {

    private static Map<String, XmlNodeProcess> processes = new HashMap<String, XmlNodeProcess>();

    private static Map<Class, XmlNode> nodeCache = new HashMap<Class, XmlNode>();

    private static boolean isInit = false;

    static {
        init();
    }

    public static Map<String, XmlNodeProcess> getProcess() {

        return processes;
    }

    public static void addProcess(XmlNodeProcess process) {

        if (!processes.containsKey(process.getName())) {
            processes.put(process.getName(), process);
        }
    }

    public synchronized static void init() {

        if (!isInit) {
            isInit = true;
            addProcess(new XmlBaseNodeProcess());
            addProcess(new XmlObjectNodeProcess());
            addProcess(new XmlListNodeProcess());
            addProcess(new XmlMapNodeProcess());
            addProcess(new DefaultXmlNodeProcess());
        }
    }

    public static XmlNodeProcess getProcessByXmlNode(XmlNode xmlnode) {

        for (String k : processes.keySet()) {
            if (processes.get(k).shouldDealWith(xmlnode)) {
                return processes.get(k);
            }
        }
        return processes.get("DefaultXmlNodeProcess");
    }

    public static XmlNode getNodeFromCache(Class clazz) {

        if (!nodeCache.containsKey(clazz)) {
            try {
                nodeCache.put(clazz, XmlNodeUtils.getXmlNodeByClass(clazz));
            } catch (Exception e) {

            }
        }
        return nodeCache.get(clazz);
    }

    public static boolean containNodeCache(Class clazz) {

        return nodeCache.containsKey(clazz);
    }
}
