package com.fr.bi.etl.analysis.monitor;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by daniel on 2017/3/17.
 */
public class UETable extends SimpleTable {
    public UETable(String id) {
        super(id);
    }

    public int calHealth(Map<SimpleTable, List<TableRelation>> relationMap, long userId) {
        this.health = ERROR;
        return this.health;
    }


    @Override
    public void dealWithJSONOption(JSONObject jo, long userId) throws Exception {
        super.dealWithJSONOption(jo, userId);
        jo.put("t", 2);
    }
}
