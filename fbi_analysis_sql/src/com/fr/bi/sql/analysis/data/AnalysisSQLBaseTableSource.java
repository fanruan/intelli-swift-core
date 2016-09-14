package com.fr.bi.sql.analysis.data;

import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.sql.analysis.Constants;
import com.fr.bi.stable.data.db.IPersistentTable;
import com.fr.bi.stable.data.db.PersistentField;
import com.fr.bi.stable.data.db.PersistentTable;
import com.fr.bi.stable.data.source.AbstractTableSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.BIDBUtils;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.List;
import java.util.Set;


/**
 * Created by 小灰灰 on 2015/12/21.
 */
public class AnalysisSQLBaseTableSource extends AbstractTableSource implements AnalysisSQLTableSource {
    @BICoreField
    private List<AnalysisSQLSourceField> fieldList;
    @BICoreField
    private String dbName;
    @BICoreField
    private String tableName;


    public AnalysisSQLBaseTableSource(String dbName, String tableName, List<AnalysisSQLSourceField> fieldList) {
        this.dbName = dbName;
        this.tableName = tableName;
        this.fieldList = fieldList;
    }


    @Override
    public IPersistentTable getPersistentTable() {
        if (dbTable == null) {
            PersistentTable table = BIDBUtils.getDBTable(dbName, tableName);
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
        return Constants.TABLE_TYPE.SQL_BASE;
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
    public void getSourceUsedAnalysisSQLSource(Set<AnalysisSQLTableSource> sources, Set<AnalysisSQLTableSource> helper) {

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
        jo.put(Constants.TABLE_NAME, tableName);
        jo.put(Constants.DB_NAME, dbName);
        return jo;
    }

    /**
     * @return
     */
    @Override
    public Set<CubeTableSource> getSourceUsedBaseSource(Set<CubeTableSource> set, Set<CubeTableSource> helper) {
        return set;
    }
}