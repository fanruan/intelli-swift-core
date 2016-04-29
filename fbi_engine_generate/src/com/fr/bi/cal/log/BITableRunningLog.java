package com.fr.bi.cal.log;

import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.Table;
import com.fr.json.JSONObject;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

public class BITableRunningLog extends BITableLog {

    public static final String XML_TAG = "bi_table_running_log";
    /**
     *
     */
    private static final long serialVersionUID = 3851669804293254268L;
    //秒
    private long seconds;

    //百分比的值
    private int row;

    public BITableRunningLog() {
    }

    public BITableRunningLog(Table table, long seconds, int row, long userId) {
        super(table, userId);
        this.seconds = seconds;
        this.row = row;
    }

    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isAttr()) {
            seconds = reader.getAttrAsLong("time", -1L);
            row = reader.getAttrAsInt("row", -1);
            this.setID(new BITableID(reader.getAttrAsString("id", "")));
        }
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        writer.attr("time", seconds);
        writer.attr("row", row);
        writer.attr("id", this.getID().getIdentityValue());
        writer.end();
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = super.createJSON();
        jo.put("time", seconds);
        jo.put("row", row);
        return jo;
    }

    @Override
    public boolean isRunning() {
        return true;
    }

    @Override
    public long getTotalTime() {
        return Math.max(seconds, 0L);
    }

}