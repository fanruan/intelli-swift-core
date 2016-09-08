package com.fr.bi.sql.analysis.data;

import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.sql.analysis.Constants;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.db.IPersistentTable;
import com.fr.bi.stable.data.db.PersistentField;
import com.fr.bi.stable.data.db.PersistentTable;
import com.fr.bi.stable.data.source.AbstractTableSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.List;
import java.util.Set;

/**
 * Created by 小灰灰 on 2016/8/22.
 */
public class AnalysisSQLIDTableSource extends AbstractTableSource implements AnalysisSQLTableSource{
    @BICoreField
    private BITableID tableID;

    @BICoreField
    private List<AnalysisSQLSourceField> fieldList;
    private AnalysisSQLTableSource source;

    public AnalysisSQLIDTableSource(BITableID tableID, List<AnalysisSQLSourceField> fieldList, AnalysisSQLTableSource source) {
        this.tableID = tableID;
        this.fieldList = fieldList;
        this.source = source;
    }
    @Override
    public List<AnalysisSQLSourceField> getFieldsList() {
        return fieldList;
    }

    @Override
    public String toSql() {
        return null;
    }

    @Override
    public IPersistentTable getPersistentTable() {
        if (dbTable == null) {
            IPersistentTable table = source.getPersistentTable();
            dbTable = new PersistentTable(table.getSchema(), table.getTableName(), table.getRemark());
            for (AnalysisSQLSourceField field : fieldList){
                PersistentField f = table.getField(field.getFieldName());
                if (f != null){
                    dbTable.addColumn(table.getField(field.getFieldName()));
                } else {
                    BINonValueUtils.beyondControl("database field lost");
                }
            }
        }
        return dbTable;
    }


    @Override
    public int getType() {
        return Constants.TABLE_TYPE.SQL_ID;
    }



    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo =  super.createJSON();
        if (fieldList != null && !fieldList.isEmpty()){
            JSONArray ja = JSONArray.create();
            for (AnalysisSQLSourceField f : fieldList){
                ja.put(f.createJSON());
            }
            jo.put(Constants.FIELDS, ja);
        }
        jo.put(Constants.TABLE_ID, tableID.getIdentityValue());
        return jo;
    }

    @Override
    public void getSourceUsedAnalysisSQLSource(Set<AnalysisSQLTableSource> set , Set<AnalysisSQLTableSource> helper) {
        if (helper.contains(this)){
            return;
        }
        helper.add(this);
        set.add(source);
        source.getSourceUsedAnalysisSQLSource(set, helper);
    }

    /**
     * @return
     */
    @Override
    public Set<CubeTableSource> getSourceUsedBaseSource(Set<CubeTableSource> set, Set<CubeTableSource> helper) {
        return set;
    }
}
