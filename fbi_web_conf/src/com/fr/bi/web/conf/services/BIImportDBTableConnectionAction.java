package com.fr.bi.web.conf.services;

import com.finebi.cube.relation.BITableRelation;
import com.fr.bi.conf.data.source.DBTableSource;
import com.fr.bi.conf.data.source.TableSourceFactory;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.data.db.IPersistentTable;
import com.fr.bi.stable.data.db.PersistentField;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BIImportDBTableConnectionAction extends AbstractBIConfigureAction {


    @Override
    public String getCMD() {
        return "import_db_table_connection";
    }

    /**
     * 导入数据库关联
     *
     * @param req
     * @param res
     * @throws Exception
     */
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String newTableString = WebUtils.getHTTPRequestParameter(req, "newTables");
        String oldTableString = WebUtils.getHTTPRequestParameter(req, "oldTables");
        Map<String, DBTableSource> newTableSources = getDBSourceMap(newTableString, userId);
        Map<String, DBTableSource> oldTableSources = getDBSourceMap(oldTableString, userId);
        Map<String, String> allFieldIdMap = new HashMap<String, String>();
        JSONObject newTablesJo = new JSONObject(newTableString);
        JSONObject oldTablesJo = new JSONObject(oldTableString);
        initFieldIdsMap(allFieldIdMap, newTablesJo);
        initFieldIdsMap(allFieldIdMap, oldTablesJo);

        JSONArray relations = getJSONArrayRelationByTables(
                new BIImportDBTableConnectionExecutor().getRelationsByTables(newTableSources, oldTableSources, allFieldIdMap, userId));
        JSONObject translations = getTranslationsByTables(newTableSources, allFieldIdMap);
        JSONObject jo = new JSONObject();
        jo.put("relations", relations);
        jo.put("translations", translations);
        WebUtils.printAsJSON(res, jo);
    }


    private JSONArray getJSONArrayRelationByTables(Set<BITableRelation> relationsSet) throws Exception {
        JSONArray relations = new JSONArray();
        Iterator<BITableRelation> relationIterator = relationsSet.iterator();
        while (relationIterator.hasNext()) {
            relations.put(relationIterator.next().createJSON());//生成JSON并放入JSONARRAY中
        }
        return relations;
    }


    private Map<String, DBTableSource> getDBSourceMap(String tableJO, long userId) throws Exception {
        Map<String, DBTableSource> sources = new HashMap<String, DBTableSource>();
        JSONObject jo = new JSONObject(tableJO);
        Iterator<String> iterator = jo.keys();
        while (iterator.hasNext()) {
            String id = iterator.next();
            CubeTableSource source = TableSourceFactory.createTableSource(jo.getJSONObject(id), userId);
            if (source.getType() == BIBaseConstant.TABLETYPE.DB) {
                sources.put(id, (DBTableSource) source);
            }
        }
        return sources;
    }

    /**
     * 读取数据库转义
     *
     * @param sourceTables
     * @return
     * @throws Exception
     */
    private JSONObject getTranslationsByTables(Map<String, DBTableSource> sourceTables, Map<String, String> allFieldIdMap) throws Exception {
        JSONObject jo = new JSONObject();
        JSONObject tableTrans = new JSONObject();
        JSONObject fieldTrans = new JSONObject();
        jo.put(BIJSONConstant.JSON_KEYS.TABLE, tableTrans);
        jo.put(BIJSONConstant.JSON_KEYS.FIELD, fieldTrans);
        Iterator<Map.Entry<String, DBTableSource>> it = sourceTables.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, DBTableSource> entry = it.next();
            IPersistentTable table = entry.getValue().getPersistentTable();
            if (!StringUtils.isEmpty(StringUtils.trim(table.getRemark()))) {
                tableTrans.put(entry.getKey(), table.getRemark());
            }
            for (PersistentField column : table.getFieldList()) {
                if (!StringUtils.isEmpty(StringUtils.trim(column.getRemark()))) {
                    fieldTrans.put(allFieldIdMap.get(entry.getKey() + column.getFieldName()), column.getRemark());
                }
            }
        }
        return jo;
    }

    private void initFieldIdsMap(Map<String, String> allFieldIdsMap, JSONObject tablesJson) throws JSONException {
        Iterator it = tablesJson.keys();
        while (it.hasNext()) {
            String tableId = it.next().toString();
            JSONObject tableJo = tablesJson.getJSONObject(tableId);
            if (tableJo.has("fields")) {
                JSONArray fieldsJa = tableJo.optJSONArray("fields");
                JSONArray stringFieldsJa = fieldsJa.getJSONArray(0);
                JSONArray numberFieldsJa = fieldsJa.getJSONArray(1);
                JSONArray dateFieldsJa = fieldsJa.getJSONArray(2);
                initAllFieldMap(allFieldIdsMap, tableId, stringFieldsJa);
                initAllFieldMap(allFieldIdsMap, tableId, numberFieldsJa);
                initAllFieldMap(allFieldIdsMap, tableId, dateFieldsJa);
            }
        }
    }

    private void initAllFieldMap(Map<String, String> allFieldIdsMap, String tableId, JSONArray stringFieldsJa) throws JSONException {
        for (int i = 0; i < stringFieldsJa.length(); i++) {
            JSONObject fieldJo = stringFieldsJa.getJSONObject(i);
            String fieldName = fieldJo.getString("field_name");
            String fieldId = fieldJo.getString("id");
            allFieldIdsMap.put(tableId + fieldName, fieldId);
        }
    }


}