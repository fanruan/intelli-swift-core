package com.fr.bi.cal.log;

import com.fr.bi.stable.data.BIBasicTable;
import com.fr.bi.stable.data.Table;
import com.fr.json.JSONCreator;
import com.fr.json.JSONObject;

public abstract class BITableLog extends BIBasicTable implements JSONCreator {

    /**
     *
     */
    private static final long serialVersionUID = -2760613466090442034L;
    protected long userId;

    public BITableLog(Table table, long userId) {
        super(table);
        this.userId = userId;
    }

    public BITableLog() {

    }

    public abstract boolean isRunning();

    public abstract long getTotalTime();

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = super.createJSON();
        jo.put("total_time", getTotalTime());
        return jo;
    }

}