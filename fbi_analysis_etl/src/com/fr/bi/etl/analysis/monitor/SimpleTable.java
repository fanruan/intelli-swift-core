package com.fr.bi.etl.analysis.monitor;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
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
public class SimpleTable {


    protected static final int ERROR = 0;
    protected static final int GOOD = 1;
    protected static final int WARNING = 2;
    protected static final int GENERATING = 3;

    private String id;

    public SimpleTable(String id) {
        this.id = id;
    }

    public String toString(){
        return id == null ? StringUtils.EMPTY : id.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleTable biTable = (SimpleTable) o;

        return  ComparatorUtils.equals(id, biTable.id);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public String getId() {
        return id;
    }

    public void dealWithJSONOption(JSONObject jo, long userId) throws Exception {
        String name = BICubeConfigureCenter.getAliasManager().getAliasName(id, userId);
        if(name == null){
            name = BIAnalysisETLManagerCenter.getAliasManagerProvider().getAliasName(id, userId);
        }
        name = StringUtils.isEmpty(name) ? id : name;
        jo.put("name", name);
        if(!isDeleted()) {
            jo.put("p", BIAnalysisTableHelper.getTableGeneratingProcessById(id, userId));
            jo.put("count", BIAnalysisTableHelper.getTableCubeCount(id, userId));
        }
    }

    public boolean isDeleted() {
        return BIModuleUtils.getBusinessTableById(new BITableID(id)) == null;
    }

    public int getHealth( Map<SimpleTable, List<TableRelation>> relationMap, long userId) {
        boolean health = BIAnalysisTableHelper.getTableHealthById(id, userId);
        int h = GOOD;
        List<TableRelation> relations = relationMap.get(this);
        if(relations.isEmpty()){
            h = health ? GOOD : ERROR;
        } else {
            for (TableRelation r : relations) {
                if (r.getTop().isDeleted()) {
                    h = WARNING;
                    break;
                } else {
                    h = health ? GOOD : GENERATING;
                }
            }
        }
        return h;
    }
}
