package com.fr.bi.conf.log;

import com.fr.bi.stable.data.db.IPersistentTable;
import com.fr.json.JSONObject;

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


    public BITableRunningLog(IPersistentTable table, long seconds, int row, long userId) {
        super(table, userId);
        this.seconds = seconds;
        this.row = row;
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