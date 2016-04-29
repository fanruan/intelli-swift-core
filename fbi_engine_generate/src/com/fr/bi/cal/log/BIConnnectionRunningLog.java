package com.fr.bi.cal.log;

import com.fr.bi.conf.report.widget.RelationColumnKey;
import com.fr.json.JSONObject;

public class BIConnnectionRunningLog extends BIConnectionCorrectLog {

    private int percent;

    public BIConnnectionRunningLog(RelationColumnKey ck, long t, int percent, long userId) {
        super(ck, t, userId);
        this.percent = percent;
    }

    public BIConnnectionRunningLog(long userId) {
        super(userId);
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
        jo.put("percent", percent);
        return jo;
    }

    /**
     * 正在运行
     *
     * @return 是否正在运行
     */
    @Override
    public boolean isRunning() {
        return true;
    }
}