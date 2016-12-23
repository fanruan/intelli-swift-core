package com.fr.bi.web.conf.services.tables;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.pack.data.BIPackageID;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.conf.utils.BIConfUtils;
import com.finebi.cube.relation.BITableRelation;
import com.fr.bi.conf.data.source.DBTableSource;
import com.fr.bi.conf.data.source.TableSourceFactory;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.bi.web.conf.utils.BIReadRelationTranslationUtils;
import com.fr.bi.web.conf.utils.BIWriteConfigResourcesUtils;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 添加表
 * 包含读关联转义
 * Created by Young's on 2016/12/16.
 */
public class BIAddNewTablesAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        final long userId = ServiceUtils.getCurrentUserID(req);
        String tablesStr = WebUtils.getHTTPRequestParameter(req, "tables");
        String packageId = WebUtils.getHTTPRequestParameter(req, "packageId");
        String exRelations = WebUtils.getHTTPRequestParameter(req, "relations");        //继承的关联
        String exTranslations = WebUtils.getHTTPRequestParameter(req, "translations");  //继承的转义

        JSONArray tablesJA = new JSONArray(tablesStr);
        Map<String, String> allFieldIdMap = new HashMap<String, String>();
        initFieldIdsMapByTables(allFieldIdMap, tablesJA);
        initFieldIdsMapByPackId(userId, packageId, allFieldIdMap);
        Map<String, DBTableSource> oldDBSourceMap = getDBSourceByPackageId(userId, packageId);
        Map<String, DBTableSource> newDBSourceMap = saveNewTable(userId, packageId, tablesJA);

        JSONArray relations = readRelation(newDBSourceMap, oldDBSourceMap, allFieldIdMap);
        BIReadRelationTranslationUtils.saveRelation(userId, relations, null);

        JSONObject translations = BIReadRelationTranslationUtils.readTranslation(newDBSourceMap, allFieldIdMap);
        saveNewTranslation(userId, translations, exTranslations);

        BIWriteConfigResourcesUtils.writeResourceAsync(userId);

        JSONObject jo = new JSONObject();
        jo.put("relations", relations);
        jo.put("translations", translations);
        WebUtils.printAsJSON(res, jo);

    }

    private JSONArray readRelation(Map<String, DBTableSource> newDBSourceMap, Map<String, DBTableSource> oldDBSourceMap, Map<String, String> allFieldIdMap) throws Exception {
        Set<BITableRelation> relationSet = BIReadRelationTranslationUtils.readRelation(newDBSourceMap, oldDBSourceMap, allFieldIdMap);
        JSONArray relations = new JSONArray();
        Iterator<BITableRelation> relationIterator = relationSet.iterator();
        while (relationIterator.hasNext()) {
            relations.put(relationIterator.next().createJSON());
        }
        return relations;
    }

    private Map<String, DBTableSource> getDBSourceByPackageId(long userId, String packageId) throws Exception {
        Map<String, DBTableSource> map = new HashMap<String, DBTableSource>();
        Set<BusinessTable> tables = BICubeConfigureCenter.getPackageManager().getPackage(userId, new BIPackageID(packageId)).getBusinessTables();
        for (BusinessTable table : tables) {
            CubeTableSource tableSource = table.getTableSource();
            if (ComparatorUtils.equals(tableSource.getType(), BIBaseConstant.TABLETYPE.DB)) {
                map.put(table.getID().getIdentityValue(), (DBTableSource) tableSource);
            }
        }
        return map;
    }

    private void initFieldIdsMapByTables(Map<String, String> allFieldIdsMap, JSONArray tablesJA) throws JSONException {
        for (int i = 0; i < tablesJA.length(); i++) {
            JSONObject tableJo = tablesJA.getJSONObject(i);
            if (tableJo.has("fields")) {
                JSONArray fieldsJa = tableJo.optJSONArray("fields");
                String tableId = tableJo.getString("id");
                initAllFieldMap(allFieldIdsMap, tableId, fieldsJa);
            }
        }
    }

    private void initFieldIdsMapByPackId(long userId, String packId, Map<String, String> allFieldIdsMap) throws Exception {
        Set<BIBusinessTable> tables = BICubeConfigureCenter.getPackageManager().getPackage(userId, new BIPackageID(packId)).getBusinessTables();
        for (BIBusinessTable table : tables) {
            JSONObject tableJO = table.createJSONWithFieldsInfo(userId);
            JSONObject tableFields = tableJO.getJSONObject("tableFields");
            JSONArray fields = tableFields.getJSONArray("fields");
            initAllFieldMap(allFieldIdsMap, table.getID().getIdentityValue(), fields);
        }
    }

    private void initAllFieldMap(Map<String, String> allFieldIdsMap, String tableId, JSONArray fields) throws JSONException {
        for (int i = 0; i < fields.length(); i++) {
            JSONArray typeFields = fields.getJSONArray(i);
            for (int j = 0; j < typeFields.length(); j++) {
                JSONObject fieldJo = typeFields.getJSONObject(j);
                String fieldName = fieldJo.getString("field_name");
                String fieldId = fieldJo.getString("id");
                allFieldIdsMap.put(tableId + fieldName, fieldId);
            }
        }
    }

    /**
     * 保存表
     *
     * @param userId
     * @param packageId
     * @param tables
     * @return
     * @throws Exception
     */
    private Map<String, DBTableSource> saveNewTable(long userId, String packageId, JSONArray tables) throws Exception {
        Map<String, DBTableSource> map = new HashMap<String, DBTableSource>();
        for (int i = 0; i < tables.length(); i++) {
            JSONObject tableJO = tables.getJSONObject(i);
            String tableId = tableJO.getString("id");
            BIBusinessTable table = new BIBusinessTable(new BITableID(tableId), tableJO.getString("table_name"));
            if (tableJO.has("fields")) {
                List<BusinessField> fields = BIConfUtils.parseField(tableJO.getJSONArray("fields"), table);
                table.setFields(fields);
            }
            CubeTableSource tableSource = TableSourceFactory.createTableSource(tableJO, userId);
            BICubeConfigureCenter.getDataSourceManager().addTableSource(table, tableSource);
            table.setSource(tableSource);
            BICubeConfigureCenter.getPackageManager().addTable(userId, new BIPackageID(packageId), table);
            if (ComparatorUtils.equals(tableSource.getType(), BIBaseConstant.TABLETYPE.DB)) {
                map.put(tableId, (DBTableSource) tableSource);
            }
        }
        return map;
    }

    private void saveNewTranslation(long userId, JSONObject translationJO, String exTranslations) throws Exception {
        JSONObject tableTranJO = translationJO.getJSONObject(BIJSONConstant.JSON_KEYS.TABLE);
        JSONObject fieldTranJO = translationJO.getJSONObject(BIJSONConstant.JSON_KEYS.FIELD);
        if (exTranslations != null) {
            JSONObject exTransJO = new JSONObject(exTranslations);
            tableTranJO.join(exTransJO);
        }
        Iterator<String> tableIds = tableTranJO.keys();
        while (tableIds.hasNext()) {
            String tranId = tableIds.next();
            String tranName = tableTranJO.optString(tranId);
            BICubeConfigureCenter.getAliasManager().getTransManager(userId).setTransName(tranId, tranName);
        }
        Iterator<String> fieldIds = fieldTranJO.keys();
        while (fieldIds.hasNext()) {
            String tranId = fieldIds.next();
            String tranName = fieldTranJO.optString(tranId);
            BICubeConfigureCenter.getAliasManager().getTransManager(userId).setTransName(tranId, tranName);
        }
    }

    @Override
    public String getCMD() {
        return "add_new_tables";
    }
}
