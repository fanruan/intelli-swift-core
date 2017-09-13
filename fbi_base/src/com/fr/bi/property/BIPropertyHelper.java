package com.fr.bi.property;

import com.fr.general.ComparatorUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLable;
import com.fr.stable.xml.XMLableReader;

import java.util.ArrayList;
import java.util.List;

public class BIPropertyHelper implements XMLable {

    public static final String XML_TAG = "SystemConfiguration";
    private List<PropertiesConfig> propertyList = new ArrayList<PropertiesConfig>();
    private PropertiesConfig property;
    private static BIPropertyHelper biPropertyHelper;

    private BIPropertyHelper(){}

    public static BIPropertyHelper getInstance () {
        if (biPropertyHelper == null) {
            biPropertyHelper = new BIPropertyHelper();
        }
        return biPropertyHelper;
    }

    @Override
    public void readXML(XMLableReader reader) {
        String tagName = reader.getTagName();
        if (ComparatorUtils.equals("SystemConfiguration", tagName)) {
            property = new PropertiesConfig();
            reader.readXMLObject(this.property);
            propertyList = property.getPropertyList();
            propertyList.add(property);
        }
    }

    @Override
    public void writeXML(XMLPrintWriter xmlPrintWriter) {

    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public PropertiesConfig getProperty() {
        return property;
    }

    public List<PropertiesConfig> getPropertyList() {
        return propertyList;
    }
}
