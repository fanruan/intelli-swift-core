package com.fr.bi.cal.log;

import com.fr.json.JSONCreator;
import com.fr.json.JSONObject;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLable;
import com.fr.stable.xml.XMLableReader;


public class BIColumnLog implements XMLable, JSONCreator {
    public static final String XML_TAG = "bi_column_log";
    /**
     *
     */
    private static final long serialVersionUID = -5900121419200014707L;
    protected long t;
    protected String columnName;

    public BIColumnLog() {
    }


    public BIColumnLog(String columnName, long t) {
        this.columnName = columnName;
        this.t = t;
    }

    public String getColumnName() {
        return columnName;
    }

    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isAttr()) {
            t = reader.getAttrAsLong("t", -1L);
            columnName = reader.getAttrAsString("name", "");
        }
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        writer.attr("name", columnName);
        writer.attr("t", t);
        writer.end();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("name", columnName);
        jo.put("time", t);
        return jo;
    }

    public boolean isRunning() {
        return false;
    }

    public long getTime() {
        return t;
    }
}