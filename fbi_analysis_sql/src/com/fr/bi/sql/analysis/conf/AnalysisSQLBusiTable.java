package com.fr.bi.sql.analysis.conf;

import com.finebi.cube.conf.field.BIBusinessField;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.fr.bi.sql.analysis.Constants;
import com.fr.bi.sql.analysis.manager.BIAnalysisSQLManagerCenter;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.BIFieldID;
import com.fr.bi.stable.data.db.PersistentField;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.BIDBUtils;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.Inter;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 小灰灰 on 2015/12/11.
 */
public class AnalysisSQLBusiTable extends BIBusinessTable {

    private String describe;
    private long userId;

    public AnalysisSQLBusiTable(String id, long userId) {
        super(id, "");
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }

    public void setSource(CubeTableSource source) {
        this.source = source;
        initFields();
    }


    private void initFields() {
        String tableId = getID().getIdentity();
        List<BusinessField> fields = new ArrayList<BusinessField>();
        for (PersistentField f : source.getPersistentTable().getFieldList()){
            fields.add(new BIBusinessField(this, new BIFieldID(tableId + f.getFieldName()), f.getFieldName(), BIDBUtils.checkColumnClassTypeFromSQL(f.getSqlType(), f.getColumnSize(), f.getScale()), f.getColumnSize()));
        }
        setFields(fields);
    }

    public CubeTableSource getSource() {
        if (source == null) {
            try {
                source = BIAnalysisSQLManagerCenter.getDataSourceManager().getTableSource(this);
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        }
        if (source == null) {
            BILogger.getLogger().info("UserEtl source missed");
        }
        return source;
    }

    public String getDescribe() {
        return describe;
    }

    public JSONObject createJSON() throws Exception {
        JSONObject jo = super.createJSON();
        jo.put("describe", describe);
        return jo;
    }

    @Override
    public void setFields(List<BusinessField> fields) {
        super.setFields(fields);
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    protected int getTableType() {
        return Constants.BUSINESS_TABLE_TYPE.ANALYSIS_SQL_TYPE;
    }

    @Override
    public JSONObject createJSONWithFieldsInfo(long userId) throws Exception {
        JSONObject jo = createJSON();
        JSONArray ja = new JSONArray();
        jo.put("fields", ja);

        List<JSONObject> stringList = new ArrayList<JSONObject>();
        List<JSONObject> numberList = new ArrayList<JSONObject>();
        List<JSONObject> dateList = new ArrayList<JSONObject>();
        List<JSONObject> countList = new ArrayList<JSONObject>();
        JSONObject fields = new JSONObject();

        Iterator<BusinessField> it = getFields().iterator();
        while (it.hasNext()) {
            BusinessField field = it.next();
            JSONObject filedJson = field.createJSON();
            fields.put(field.getFieldID().getIdentityValue(), filedJson);
            stringList.add(filedJson);
        }
        fields.put(getID().getIdentity() + BIAnalysisSQLManagerCenter.getAliasManagerProvider().getTransManager(userId).getTransName(getID().getIdentityValue()) + Inter.getLocText("BI-Records"), createCountField(userId));
        countList.add(createCountField(userId));
        ja.put(stringList).put(numberList).put(dateList).put(countList);
        JSONObject result = new JSONObject();
        result.put("tableFields", jo);
        result.put("fieldsInfo", fields);
        jo.put(BIJSONConstant.JSON_KEYS.TABLE_TYPE, getTableType());
        return result;
    }

    private JSONObject createCountField(long userId) throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("field_type", DBConstant.COLUMN.COUNTER);
        jo.put("field_name", BIAnalysisSQLManagerCenter.getAliasManagerProvider().getTransManager(userId).getTransName(getID().getIdentityValue()) + Inter.getLocText("BI-Records"));
        jo.put("table_id", getID().getIdentity());
        jo.put("is_usable", true);
        //记录数的id先暂时用拼接
        jo.put("id", jo.optString("table_id") +BIAnalysisSQLManagerCenter.getAliasManagerProvider().getTransManager(userId).getTransName(getID().getIdentityValue()) + Inter.getLocText("BI-Records"));
        return jo;
    }

}