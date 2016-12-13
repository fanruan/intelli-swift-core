package com.fr.bi.conf.log;

import com.fr.bi.conf.report.widget.RelationColumnKey;
import com.fr.bi.stable.constant.BILogConstant;
import com.fr.json.JSONObject;

public class BIConnectionErrorLog extends BIConnectionLog implements ErrorLog {

    private String error_text;

    public BIConnectionErrorLog(long userId) {
        super(userId);
    }

    public BIConnectionErrorLog(RelationColumnKey ck, String error_text, long userId) {
        super(ck, userId);
        this.error_text = error_text;
    }

    public BIConnectionErrorLog() {
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = super.createJSON();
        jo.put("error_text", error_text);
        return jo;
    }

    public String getError_text() {
        return error_text;
    }

    @Override
    public long getTime() {
        return 0;
    }

    @Override
    public int getType() {
        return BILogConstant.PATH_LOG_TYPE.ERROR;
    }
}