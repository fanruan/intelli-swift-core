package com.fr.bi.common.persistent;

import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by neil on 17-3-20.
 */
public class BIBeanHistoryManager {

    protected static final String DEFAULT_FILE_NAME = "bean_history_class.xml";

    public static String getDefaultFileName() {
        return Thread.currentThread().getContextClassLoader().getResource("").getPath() + DEFAULT_FILE_NAME;
    }

    private static Map<String, List<String>> getClassMapping(String beanFilePath) {
        Map<String, List<String>> classMapping = new HashMap<String, List<String>>();

        if(beanFilePath !=null && new File(beanFilePath).exists()) {
            Document document = readFile(beanFilePath);
            NodeList beanList = document.getElementsByTagName("bean");
            for (int i = 0; i < beanList.getLength(); i++) {
                Node bean = beanList.item(i);

                Node historyClasses = ((DeferredElementImpl) bean).getElementsByTagName("historyClasses").item(0);
                classMapping.put(((DeferredElementImpl) bean).getAttribute("currentClass"), parseHistoryClass(historyClasses));
            }
        }
        return classMapping;
    }

    public static String getCurrentClassName(String oldClass, String beanFilePath) {
        Map<String, List<String>> classMapping = getClassMapping(beanFilePath);

        for (Map.Entry<String, List<String>> entry : classMapping.entrySet()) {
            List<String> oldClasses = entry.getValue();
            if (oldClasses.contains(oldClass)) {
                return entry.getKey();
            }
        }
        return oldClass;
    }

    private static Document readFile(String beanFilePath) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            File file = new File(beanFilePath);
            return db.parse(file);
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e.getMessage(), e);
        }
    }

    private static List<String> parseHistoryClass(Node historyClasses) {
        List<String> classNames = new ArrayList<String>();
        NodeList nodeList = ((DeferredElementImpl) historyClasses).getElementsByTagName("class");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            classNames.add(((DeferredElementImpl) node).getAttribute("value"));
        }
        return classNames;
    }

}
