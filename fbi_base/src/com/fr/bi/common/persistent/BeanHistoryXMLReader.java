package com.fr.bi.common.persistent;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by neil on 2017/4/12.
 */
public class BeanHistoryXMLReader {

    public Map<String, List<String>> loadBeanHistoryMap(InputStream inputStream) throws Exception{
        Map<String, List<String>> classMapping = new HashMap<String, List<String>>();

        if(inputStream !=null ) {
            Document document = readFile(inputStream);
            NodeList beanList = document.getElementsByTagName("bean");
            for (int i = 0; i < beanList.getLength(); i++) {
                Node bean = beanList.item(i);

                Node historyClasses = ((Element) bean).getElementsByTagName("historyClasses").item(0);
                classMapping.put(((Element) bean).getAttribute("currentClass"), parseHistoryClass(historyClasses));
            }
        }
        return classMapping;
    }

    private Document readFile(InputStream inputStream) throws Exception{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.parse(inputStream);
    }

    private List<String> parseHistoryClass(Node historyClasses) {
        List<String> classNames = new ArrayList<String>();
        NodeList nodeList = ((Element) historyClasses).getElementsByTagName("class");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            classNames.add(((Element) node).getAttribute("value"));
        }
        return classNames;
    }
}
