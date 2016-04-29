package com.fr.bi.cal.log;

import com.fr.bi.conf.report.widget.RelationColumnKey;
import com.fr.json.JSONObject;

public class BIConnectionCorrectLog extends BIConnectionLog {

    public static String XML_TAG = "bi_connection_correct_log";

    private long seconds;

    public BIConnectionCorrectLog(RelationColumnKey ck, long seconds, long userId) {
        super(ck, userId);
        this.seconds = seconds;
    }

    public BIConnectionCorrectLog(long userId) {
        super(userId);
    }

    public BIConnectionCorrectLog() {
    }

    /**
     * 将Java对象转换成JSON对象
     *
     * @return JSON对象
     * @throws Exception
     */
    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = super.createJSON();
        jo.put("time", seconds);
        return jo;
    }

    @Override
    public long getTime() {
        return seconds;
    }

}