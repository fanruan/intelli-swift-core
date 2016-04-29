package com.fr.bi.conf.fs.develop.enviroment;

import com.fr.bi.conf.fs.develop.DeveloperConfig;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Connery on 2015/1/9.
 */
public class WidgetExpanderRecorder {
    private static WidgetExpanderRecorder instance;
    private String path = DeveloperConfig.getTestBasicPath() + DeveloperConfig.FILESEPARATOR + "expander" + DeveloperConfig.FILESEPARATOR;
    private ArrayList<WidgetExpanderItem> items;
    private Document document;
    private Element root;

    private WidgetExpanderRecorder() {
        items = new ArrayList<WidgetExpanderItem>();

        DocumentBuilderFactory factory = DocumentBuilderFactory
                .newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            this.document = builder.newDocument();
        } catch (Exception ex) {

        }
        root = this.document.createElement("root");
        this.document.appendChild(root);
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static WidgetExpanderRecorder getInstance() {
        if (instance == null) {
            instance = new WidgetExpanderRecorder();
        }
        return instance;
    }

    public ArrayList<WidgetExpanderItem> getItems() {
        return items;
    }

    public void addRecord(String widgetName,
                          boolean isExpander,
                          int regionIndex,
                          String expanderString,
                          String clickedDimensionString) {
        WidgetExpanderItem widgetExpanderItem = new WidgetExpanderItem(widgetName, isExpander, regionIndex, expanderString, clickedDimensionString);
        items.add(widgetExpanderItem);
    }

    public void save(String fileName) {
        Iterator<WidgetExpanderItem> it = items.iterator();
        while (it.hasNext()) {
            WidgetExpanderItem item = it.next();
            try {
                Element expand = document.createElement("expander");
                expand = item.createXML(this.document, expand);
                root.appendChild(expand);
            } catch (Exception ex) {
                continue;
            }
        }
        ReportIDRegister.writeXML(path + fileName, document);
        items.clear();
    }

    public void read(String fileName) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        items.clear();
        ArrayList<String> ids = new ArrayList<String>();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(path + fileName + ".xml");
            NodeList nodelist = document.getElementsByTagName("expander");
            for (int i = 0; i < nodelist.getLength(); i++) {
                WidgetExpanderItem widgetExpanderItem = new WidgetExpanderItem();
                widgetExpanderItem.initial((Element) nodelist.item(i));
                items.add(widgetExpanderItem);
            }
        } catch (Exception ex) {

        }

    }

}