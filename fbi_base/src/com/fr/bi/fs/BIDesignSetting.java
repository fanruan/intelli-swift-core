package com.fr.bi.fs;

import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.xml.XMLFileReader;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLable;
import com.fr.stable.xml.XMLableReader;

/**
 * Created by 小灰灰 on 2015/7/16.
 */
public class BIDesignSetting implements XMLable, XMLFileReader {
    private static final long serialVersionUID = 1814545660669230061L;
    private String reportSetting;


    public BIDesignSetting(String reportSetting) {
        this.reportSetting = reportSetting;
    }

    public BIDesignSetting() {

    }

    @Override
    public Object readFileContent(XMLableReader var1) {
        BIDesignSetting var2 = new BIDesignSetting();
        var1.readXMLObject(var2);
        return var2;
    }

    @Override
    public Object errorHandler() {
        return new BIDesignSetting();
    }

    public JSONObject getReportSetting() throws JSONException {
        return new JSONObject(this.reportSetting);
    }


    @Override
    public void readXML(XMLableReader var1) {
        if (var1.isChildNode() && "reportSetting".equals(var1.getTagName())) {
            this.reportSetting = var1.getAttrAsString("setting", "{}");
        }

    }

    @Override
    public void writeXML(XMLPrintWriter var1) {
        var1.startTAG("ADHOCReportSetting");
        if (this.reportSetting != null) {
            var1.startTAG("reportSetting").attr("setting", this.reportSetting).end();
        }

        var1.end();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }


}