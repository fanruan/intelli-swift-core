package com.fr.bi.etl.analysis.monitor;

import com.fr.bi.base.BIUser;
import com.fr.bi.etl.analysis.data.AnalysisETLOperatorFactory;
import com.fr.bi.etl.analysis.data.AnalysisETLTableSource;
import com.fr.bi.etl.analysis.manager.BIAnalysisETLManagerCenter;
import com.fr.bi.web.service.utils.BIAnalysisTableHelper;
import com.fr.json.JSONObject;
import com.fr.stable.core.UUID;

import java.util.List;
import java.util.Map;

/**
 * Created by daniel on 2017/2/6.
 */
public class SimpleSourceTable extends SimpleTable {
    private AnalysisETLTableSource source;

    public SimpleSourceTable(AnalysisETLTableSource source) {
        super(UUID.randomUUID().toString());
        this.source = source;
    }


    public void dealWithJSONOption(JSONObject jo, long userId) throws Exception {
        if(source != null) {
            JSONObject jt = JSONObject.create();
            AnalysisETLOperatorFactory.createJSONByOperators(jt, source.getETLOperators());
            jo.put("etlType", jt.get("etlType"));
            try {
                jo.put("count", BIAnalysisETLManagerCenter.getUserETLCubeManagerProvider().getThreadPoolCubeCount(source, new BIUser(userId)));
                jo.put("p", BIAnalysisTableHelper.getPercent(source, userId));
            } catch (Exception e){
                jo.put("p", 0);
            }
        } else {
            jo.put("name", "source has been deleted");
            jo.put("p", 0);
            jo.put("count", 0);
        }
    }

    public boolean isDeleted() {
        return source == null;
    }

    public int calHealth(Map<SimpleTable, List<TableRelation>> relationMap, long userId) {
        if(source == null){
            this.health = ERROR;
            return ERROR;
        }
        int h = GOOD;
        boolean health = BIAnalysisETLManagerCenter.getUserETLCubeManagerProvider().isAvailable(source, new BIUser(userId));
        List<TableRelation> relations = relationMap.get(this);
        h = calParent(health, h, relations);
        this.health = h;
        return h;
    }
}
