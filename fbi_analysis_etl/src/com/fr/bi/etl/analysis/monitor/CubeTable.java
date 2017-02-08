package com.fr.bi.etl.analysis.monitor;

import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.cal.stable.loader.CubeReadingTableIndexLoader;
import com.fr.bi.conf.utils.BIModuleUtils;
import com.fr.bi.etl.analysis.manager.BIAnalysisETLManagerCenter;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.web.service.utils.BIAnalysisTableHelper;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by daniel on 2017/1/22.
 */
public class CubeTable extends SimpleTable {

    public CubeTable(String id) {
        super(id);
    }

    @Override
    public void dealWithJSONOption(JSONObject jo, long userId) throws Exception {
        String name = BICubeConfigureCenter.getAliasManager().getAliasName(getId(), userId);
        name = StringUtils.isEmpty(name) ? getId() : name;
        jo.put("name", name);
        int h = checkStatus(userId);
        int v = h == GOOD ? 1:0;
        jo.put("p", v);
        jo.put("count", v);
        jo.put("t",1);
    }

    private int checkStatus (long userId) {
        if(isDeleted()){
            return ERROR;
        }
        BusinessTable table = BIModuleUtils.getBusinessTableById(new BITableID(getId()));
        ICubeTableService is = null;
        try {
            is = CubeReadingTableIndexLoader.getInstance(userId).getTableIndex(table.getTableSource());
        } catch (Exception e) {
        }
        return is == null ? WARNING : GOOD;
    }

    @Override
    public int getHealth( Map<SimpleTable, List<TableRelation>> relationMap, long userId) {
       return checkStatus(userId);
    }

}
