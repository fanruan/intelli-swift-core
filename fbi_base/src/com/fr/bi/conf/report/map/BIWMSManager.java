package com.fr.bi.conf.report.map;

import com.fr.file.XMLFileManager;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLReadable;
import com.fr.stable.xml.XMLableReader;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 2016/8/31.
 */
public class BIWMSManager extends XMLFileManager {
    private static final String XML_TAG = "BIWMSManager";

    private HashMap<String, MapInfo> nameMap = new HashMap<String, MapInfo>();

    private static BIWMSManager manager;

    public static BIWMSManager getInstance() {
        synchronized (BIWMSManager.class) {
            if (manager == null) {
                manager = new BIWMSManager();
                manager.readXMLFile();
            }
            return manager;
        }
    }

    @Override
    public String fileName() {
        return "bi_wms.xml";
    }

    @Override
    public void readXML(final XMLableReader reader) {
        if (reader.isChildNode()) {
            if (ComparatorUtils.equals(reader.getTagName(), "gis")) {

                String name = reader.getAttrAsString("name", StringUtils.EMPTY);

               final MapInfo info = new MapInfo();

                nameMap.put(name, info);

                String type = reader.getAttrAsString("type", StringUtils.EMPTY);
                info.setType(type);

                reader.readXMLObject(new XMLReadable() {
                    @Override
                    public void readXML(XMLableReader xmLableReader) {
                        if(xmLableReader.isChildNode()){
                            if(ComparatorUtils.equals(xmLableReader.getTagName(), "wmsLayer")){
                                info.addWmsLayer(xmLableReader.getElementValue());
                            }
                            if(ComparatorUtils.equals(xmLableReader.getTagName(), "url")){
                                info.setUrl(xmLableReader.getElementValue());
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public void writeXML(XMLPrintWriter xmlPrintWriter) {

    }

    public void clear(){
        nameMap.clear();
    }

    public Map<String, MapInfo> getWMSInfo(){
        return nameMap;
    }

    public JSONObject getWMSInfo(String key){
        MapInfo info = nameMap.get(key);
        return info == null ? JSONObject.create() : info.toJSON();
    }
}
