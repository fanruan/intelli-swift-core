package com.finebi.cube.conf.table;


import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.field.BusinessField;
import com.fr.bi.common.factory.IFactoryService;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.AbstractTableSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BICollectionUtils;
import com.fr.general.Inter;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2016/5/21.
 *
 * @author Connery
 * @since 4.0
 */
@BIMandatedObject(factory = IFactoryService.CONF_XML, implement = BusinessTable.class)
public class BIBusinessTable implements BusinessTable {

    protected BITableID ID;
    protected String tableName;
    protected List<BusinessField> fields;
    protected CubeTableSource source;

    public BIBusinessTable(BITableID ID) {
        this(ID, "FINEBI_EMPTY");
    }

    public BIBusinessTable(BITableID ID, String tableName) {
        setID(ID);
        this.tableName = tableName;
    }

    public BIBusinessTable(String ID, String tableName) {
        this(new BITableID(ID), tableName);
    }

    @Override
    public BITableID getID() {
        return ID;
    }

    public void setID(BITableID ID) {
        this.ID = ID;
    }


    @Override
    public List<BusinessField> getFields() {
        return BICollectionUtils.unmodifiedCollection(fields);
    }

    @Override
    public void setSource(CubeTableSource source) {
        this.source = source;
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("id", ID.getIdentityValue());
        return jo;
    }



    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("id")) {
            this.setID(new BITableID(jo.getString("id")));
        }
    }


    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BIBusinessTable)) return false;

        BIBusinessTable that = (BIBusinessTable) o;

        return !(ID != null ? !ID.equals(that.ID) : that.ID != null);

    }

    @Override
    public int hashCode() {
        return ID != null ? ID.hashCode() : 0;
    }

    @Override
    public CubeTableSource getTableSource() {
        return source;
    }


    @Override
    public void setFields(List<BusinessField> fields) {
        this.fields = fields;
    }

    @Override
    public JSONObject createJSONWithFieldsInfo(ICubeDataLoader loader) throws Exception {
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
            /**
             * Connery:错用createJson，传递了一个Loader进去
             */
            JSONObject filedJson = field.createJSON(loader);
            fields.put(field.getFieldID().getIdentityValue(), filedJson);
            stringList.add(filedJson);
        }
        fields.put(getID().getIdentity() + BICubeConfigureCenter.getAliasManager().getTransManager(-999).getTransName(getID().getIdentityValue()) + Inter.getLocText("BI-Records"), createCountField());
        countList.add(createCountField());
        ja.put(stringList).put(numberList).put(dateList).put(countList);
        JSONObject result = new JSONObject();
        result.put("tableFields", jo);
        result.put("fieldsInfo", fields);
        jo.put(BIJSONConstant.JSON_KEYS.TABLE_TYPE, getTableType());
        return result;
    }

    private Map<String, ICubeFieldSource> getSourceFields() {
        try {
            return ((AbstractTableSource) getTableSource()).getFields();
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
            throw new RuntimeException("Please check connection");
        }

    }


    private JSONObject createCountField() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("field_type", DBConstant.COLUMN.COUNTER);
        jo.put("field_name", BICubeConfigureCenter.getAliasManager().getTransManager(-999).getTransName(getID().getIdentityValue()) + Inter.getLocText("BI-Records"));
        jo.put("table_id", getID().getIdentity());
        jo.put("is_usable", false);
        //记录数的id先暂时用拼接
        jo.put("id", jo.optString("table_id") + BICubeConfigureCenter.getAliasManager().getTransManager(-999).getTransName(getID().getIdentityValue()) + Inter.getLocText("BI-Records"));
        return jo;
    }

    protected int getTableType() {
        return BIReportConstant.BUSINESS_TABLE_TYPE.NORMAL;
    }
}
