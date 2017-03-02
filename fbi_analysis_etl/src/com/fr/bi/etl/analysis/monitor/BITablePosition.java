package com.fr.bi.etl.analysis.monitor;

import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.conf.utils.BIModuleUtils;
import com.fr.bi.etl.analysis.manager.BIAnalysisETLManagerCenter;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.web.service.utils.BIAnalysisTableHelper;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

/**
 * Created by daniel on 2017/1/22.
 */
public class BITablePosition {
    private int column = 0;
    private double row = 0;
    private final SimpleTable table;

    public BITablePosition(SimpleTable table) {
        this.table = table;
    }

    public SimpleTable getTable(){
        return  table;
    }

    public double getRow() {
        return row;
    }

    public void setRow(double row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public JSONObject createJSON(long userId) throws Exception {
        JSONObject jo = JSONObject.create();
        jo.put("id", table.getId());
        BusinessTable bt = BIModuleUtils.getBusinessTableById(new BITableID(table.getId()));
        if(bt == null) {
            jo.put("p", 0);
        }
        table.dealWithJSONOption(jo, userId);
        jo.put("c", column);
        jo.put("r", row);

        return jo;
    }
}
