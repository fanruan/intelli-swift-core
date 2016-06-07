package com.fr.bi.conf.log;

import com.fr.bi.stable.data.db.IPersistentTable;
import com.fr.json.JSONCreator;
import com.fr.json.JSONObject;

public abstract class BITableLog implements JSONCreator {

    /**
     *
     */
    private static final long serialVersionUID = -2760613466090442034L;
    protected long userId;
    private IPersistentTable persistentTable;

    public BITableLog(IPersistentTable table, long userId) {
        this.userId = userId;
        this.persistentTable = table;
    }

    public BITableLog() {

    }

    public IPersistentTable getPersistentTable() {
        return persistentTable;
    }

    public abstract boolean isRunning();

    public abstract long getTotalTime();

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = persistentTable.createJSON();
        jo.put("total_time", getTotalTime());
        return jo;
    }

}