package com.fr.bi.cal.log;

import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.Table;
import com.fr.json.JSONObject;
import com.fr.stable.CodeUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

public class BITableErrorLog extends BITableLog implements ErrorLog {


    public static final String XML_TAG = "error_log";
    /**
     *
     */
    private static final long serialVersionUID = 8303606030073599201L;
    private String error_text;

    public BITableErrorLog() {
    }

    public BITableErrorLog(Table table, String error_text, long userId) {
        super(table, userId);
        this.error_text = error_text;
    }

    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isAttr()) {
            error_text = CodeUtils.passwordDecode(reader.getAttrAsString("error_text", "xml read error"));
            this.setID(new BITableID(reader.getAttrAsString("id", "")));
        }


    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        writer.attr("error_text", CodeUtils.passwordEncode(error_text));
        writer.attr("id", this.getID().getIdentityValue());
        writer.end();
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = super.createJSON();
        jo.put("error_text", error_text);
        return jo;
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public long getTotalTime() {
        return 0;
    }
}