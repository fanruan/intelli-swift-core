package com.fr.bi.conf.base.pack.data;


import com.fr.bi.base.BIUser;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.*;
import com.fr.bi.stable.data.db.BICubeFieldSource;
import com.fr.bi.stable.data.source.AbstractTableSource;
import com.fr.bi.stable.data.source.ICubeTableSource;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.exception.BIDBConnectionException;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.Inter;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.util.*;

/**
 * Created by GUY on 2015/3/3.
 */
public class BIBusinessTable extends BITable {


    public void setSource(ICubeTableSource source) {
        this.source = source;
    }


    public BIBusinessTable(String id, Long userId) {
        super(id);
        user = new BIUser(userId);
        initialTableSource();
    }

    public BIUser getUser() {
        return user;
    }

    private void initialTableSource() {
        if (source == null) {
            source = BIConfigureManagerCenter.getDataSourceManager().getTableSourceByID(new BITableID(ID), new BIUser(-999));
        }
    }

    public int getLevel() {
        return getSource().getLevel();
    }

    public int getType() {
        return getSource().getType();
    }

    public Set<Table> createTableKeys() {
        return getSource().createTableKeys();
    }


    public Iterator<BIBasicField> getFieldsIteratorFromDB() throws BIDBConnectionException {
        Iterator<Map.Entry<String, BICubeFieldSource>> it = getSourceFieldsIterator();
        if (it == null) {
            return null;
        }
        fieldArray.clear();
        while (it.hasNext()) {
            Map.Entry<String, BICubeFieldSource> entry = it.next();
            BICubeFieldSource field = entry.getValue();
            fieldArray.add(new BIBusinessField(getID().getIdentityValue(), field.getFieldName(), field.getClassType(), field.getFieldSize()));
        }
        return fieldArray.iterator();
    }

    public Iterator<BIBasicField> getFieldsIterator() throws BIDBConnectionException {
        if (fieldArray.isEmpty()) {
            return getFieldsIteratorFromDB();
        }
        return fieldArray.iterator();
    }


    private Iterator<Map.Entry<String, BICubeFieldSource>> getSourceFieldsIterator() throws BIDBConnectionException {
        Iterator<Map.Entry<String, BICubeFieldSource>> it = null;
        try {
            it = ((AbstractTableSource) getSource()).getFields().entrySet().iterator();
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        if (it == null) {
            /**
             * TODO 应该在数据库连接处抛错，
             */
            throw new BIDBConnectionException("Please check connection");
        }
        return it;
    }

    public ICubeTableSource getSource() {
        /**
         * 依赖BIConfigureManagerCenter，代码没法移动模块了，
         * 暂时去掉，之后改成set。
         */
        initialTableSource();
        if (source == null) {
            BILogger.getLogger().info("BI source missed");
        }
        return source;
    }

    public JSONObject createJSONWithFieldsInfo(ICubeDataLoader loader) throws Exception {
        JSONObject jo = super.createJSON();
        JSONArray ja = new JSONArray();
        jo.put("fields", ja);

        List<JSONObject> stringList = new ArrayList<JSONObject>();
        List<JSONObject> numberList = new ArrayList<JSONObject>();
        List<JSONObject> dateList = new ArrayList<JSONObject>();
        List<JSONObject> countList = new ArrayList<JSONObject>();
        JSONObject fields = new JSONObject();

        Iterator<BIBasicField> it = getFieldsIterator();
        while (it.hasNext()) {
            BIBasicField field = it.next();
            /**
             * Connery:错用createJson，传递了一个Loader进去
             */
            JSONObject filedJson = field.createJSON(loader);
            fields.put(field.getTableBelongTo().getID().getIdentityValue() + field.getFieldName(), filedJson);
            stringList.add(filedJson);
        }
        fields.put(getID().getIdentity() + BIConfigureManagerCenter.getAliasManager().getTransManager(user.getUserId()).getTransName(getID().getIdentityValue()) + Inter.getLocText("BI-Records"), createCountField());
        countList.add(createCountField());
        ja.put(stringList).put(numberList).put(dateList).put(countList);
        JSONObject result = new JSONObject();
        result.put("tableFields", jo);
        result.put("fieldsInfo", fields);
        jo.put(BIJSONConstant.JSON_KEYS.TABLE_TYPE, getTableType());
        return result;
    }

    private JSONObject createCountField() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("field_type", DBConstant.COLUMN.COUNTER);
        jo.put("field_name", "");
        jo.put("table_id", getID().getIdentity());
        jo.put("id", jo.optString("table_id") + BIConfigureManagerCenter.getAliasManager().getTransManager(user.getUserId()).getTransName(getID().getIdentityValue()) + Inter.getLocText("BI-Records"));
        return jo;
    }

    protected int getTableType() {
        return BIReportConstant.BUSINESS_TABLE_TYPE.NORMAL;
    }


    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("id", ID.getIdentityValue());
        JSONArray ja = new JSONArray();
        for (int i = 0; i < usedFields.size(); i++) {
            ja.put(usedFields.get(i));
        }
        jo.put("used_fields", ja);
        return jo;
    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        ID = new BITableID(jo.optString("id", StringUtils.EMPTY));
        if (jo.has("used_fields")) {
            JSONArray ja = jo.getJSONArray("used_fields");
            for (int i = 0; i < ja.length(); i++) {
                usedFields.add(ja.getString(i));
            }
        }
    }

}