package com.fr.bi.conf.base.cube;

import com.fr.bi.conf.base.cube.data.BILoginInfoInTableField;
import com.fr.json.JSONObject;

/**
 * Created by Young's on 2016/5/19.
 */
public class BICubeConfManager {
    private String cubePath;
    private BILoginInfoInTableField tableField = new BILoginInfoInTableField();

    public String getCubePath() {
        return cubePath;
    }

    public void setCubePath(String cubePath) {
        this.cubePath = cubePath;
    }

    public BILoginInfoInTableField getTableField() {
        return tableField;
    }

    public void setTableField(BILoginInfoInTableField tableField) {
        this.tableField = tableField;
    }

    public JSONObject createJSON() throws Exception{
        JSONObject jo = new JSONObject();
        if(cubePath != null) {
            jo.put("cube_path", cubePath);
        }
        if(tableField != null) {
            jo.put("table_field", tableField.createJSON());
        }
        return  jo;
    }

}
