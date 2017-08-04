package com.fr.bi.etl.analysis.conf;

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.field.BIBusinessField;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.etl.analysis.data.AnalysisCubeTableSource;
import com.fr.bi.etl.analysis.manager.BIAnalysisETLManagerCenter;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.BIFieldID;
import com.fr.bi.stable.data.db.PersistentField;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.BIDBUtils;
import com.fr.general.Inter;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.*;

/**
 * Created by 小灰灰 on 2015/12/11.
 */
public class AnalysisBusiTable extends BIBusinessTable {
    private static BILogger LOGGER = BILoggerFactory.getLogger(AnalysisBusiTable.class);
    private static final long serialVersionUID = 5081075157518418589L;
    private String describe;
    private String name;
    private long userId;

    public AnalysisBusiTable(String id, long userId) {
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
        for (PersistentField f : source.getPersistentTable().getFieldList()) {
            fields.add(new BIBusinessField(this, new BIFieldID(tableId + f.getFieldName()), f.getFieldName(), BIDBUtils.checkColumnClassTypeFromSQL(f.getSqlType(), f.getColumnSize(), f.getScale()), f.getColumnSize()));
        }
        setFields(fields);
    }

    @Override
    public CubeTableSource getTableSource() {
        if (source == null) {
            try {
                source = BIAnalysisETLManagerCenter.getDataSourceManager().getTableSource(this);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        if (source == null) {
            LOGGER.info("UserEtl source missed");
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
        return Constants.BUSINESS_TABLE_TYPE.ANALYSIS_TYPE;
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
        //兼容以前老的记录数ID，包含表转义名的
        fields.put(getID().getIdentity() + BIAnalysisETLManagerCenter.getAliasManagerProvider().getAliasNameFromAllUsers(getID().getIdentityValue()) + Inter.getLocText("BI-Basic_Records"), createCountField(userId));
        //真正使用的记录数ID
        fields.put(getID().getIdentity() + Inter.getLocText("BI-Basic_Records"), createCountField(userId));
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
        jo.put("fieldType", DBConstant.COLUMN.COUNTER);
        jo.put("fieldName", BIAnalysisETLManagerCenter.getAliasManagerProvider().getAliasNameFromAllUsers(getID().getIdentityValue()) + Inter.getLocText("BI-Basic_Records"));
        jo.put("tableId", getID().getIdentity());
        jo.put("isUsable", true);
        //记录数的id先暂时用拼接
        jo.put("id", jo.optString("tableId") + Inter.getLocText("BI-Basic_Records"));
        return jo;
    }

    public Set<BusinessTable> getUsedTables() {
        Set<BusinessTable> usedTables = new HashSet<BusinessTable>();
        if (source != null) {
            for (BIWidget widget : ((AnalysisCubeTableSource) source).getWidgets()) {
                if (null != widget && null != widget.getUsedTableDefine()) {
                    for (BusinessTable table : widget.getUsedTableDefine()) {
                        usedTables.add(table);
                    }
                }
            }
        }

        return usedTables;
    }
}