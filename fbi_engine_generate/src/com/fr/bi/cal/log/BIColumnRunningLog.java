package com.fr.bi.cal.log;

import com.fr.json.JSONObject;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;


public class BIColumnRunningLog extends BIColumnLog {

    public static final String XML_TAG = "bi_column_running_log";
    /**
     *
     */
    private static final long serialVersionUID = 5909107821841336307L;
    private int percent;

    public BIColumnRunningLog() {
    }

    public BIColumnRunningLog(String columnName, long t, int percent) {
        super(columnName, t);
        this.percent = percent;
    }

    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isAttr()) {
            t = reader.getAttrAsLong("t", -1L);
            percent = reader.getAttrAsInt("percent", percent);
            columnName = reader.getAttrAsString("name", "");
        }
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        writer.attr("name", columnName);
        writer.attr("percent", percent);
        writer.attr("t", t);
        writer.end();
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = super.createJSON();
        jo.put("percent", percent);
        return jo;
    }

    @Override
    public boolean isRunning() {
        return true;
    }
}