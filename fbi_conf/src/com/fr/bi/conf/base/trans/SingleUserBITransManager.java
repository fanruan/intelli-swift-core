package com.fr.bi.conf.base.trans;

import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.file.XMLFileManager;
import com.fr.fs.control.UserControl;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by GUY on 2015/3/31.
 */
public class SingleUserBITransManager extends XMLFileManager {

    private static final String XML_TAG = "BITransManager";

    private final Map<String, String> transMap = new HashMap<String, String>();
    private long userId;

    public SingleUserBITransManager(long userId) {
        this.userId = userId;
        readXMLFile();
    }

    public String getTransName(String id) {
        synchronized (transMap) {
            return transMap.get(id);
        }
    }

    public void setTransName(String id, String name) {
        if (StringUtils.isEmpty(id) || StringUtils.isEmpty(name)) {
            return;
        }
        synchronized (transMap) {
            transMap.put(id, name);
        }
    }

    public JSONObject createJSON() throws Exception{
        JSONObject trans = new JSONObject();
        Iterator<Map.Entry<String, String>> it = transMap.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<String, String> entry = it.next();
            trans.put(entry.getKey(), entry.getValue());
        }
        return trans;
    }

    @Override
    public String fileName() {
        return userId == UserControl.getInstance().getSuperManagerID() ?
                "bi_trans.xml" : ("singleuserpackets" + File.separator + "bi_trans_" + userId + ".xml");
    }

    @Override
    public void readXML(XMLableReader reader) {

        if (reader.isChildNode()) {
            String tagName = reader.getTagName();
            if (ComparatorUtils.equals(tagName, "trans")) {
                String id = reader.getAttrAsString("id", StringUtils.EMPTY);
                String name = reader.getAttrAsString("name", StringUtils.EMPTY);
                if (StringUtils.isNotEmpty(id)) {
                    transMap.put(id, name);
                }
            }
        }
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        writer.attr(BIBaseConstant.VERSIONTEXT, BIBaseConstant.VERSION);
        for (Map.Entry<String, String> stringStringEntry : transMap.entrySet()) {
            writer.startTAG("trans");
            writer.attr("id", stringStringEntry.getKey());
            writer.attr("name", stringStringEntry.getValue());
            writer.end();
        }
        writer.end();
    }

    public void clear() {
        synchronized (transMap) {
            transMap.clear();
        }
    }
}