package com.fr.bi.web.conf.services;

import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BIGetFieldsInfor4BuildRelation extends AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "get_fields_infor_4_build_relation";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
//		String typeString = WebUtils.getHTTPRequestParameter(req, "fieldType");
//		String primaryTable = WebUtils.getHTTPRequestParameter(req, "primaryTable");
//		long userId = ServiceUtils.getCurrentUserID(req);
//		int fieldType = Integer.valueOf(typeString).intValue();
//		JSONObject primaryTbleJo = new JSONObject(primaryTable);
//
//		String connectionName = primaryTbleJo.getString("connection_name");
//		String schemaName = primaryTbleJo.has("schema_name") ? primaryTbleJo.getString("schema_name") : null;
//		String tableName = primaryTbleJo.getString("table_name");
//        String md5TableName = tableName;
//        if (primaryTbleJo.has("md5_table_name")){
//            md5TableName = primaryTbleJo.getString("md5_table_name");
//        }
//		String dbLink = primaryTbleJo.has("dbLink") ? primaryTbleJo.getString("dbLink") : null;
//		BIAbstractBusiTable busiTable = BIConfUtils.getBusiTable4Config(new BITableKey(connectionName, schemaName, md5TableName, tableName, dbLink), userId);
//		if (busiTable == null){
//            busiTable = new BIDBBusiTable(connectionName, schemaName, tableName, dbLink);
//        }
//        Iterator<Entry<String, BIField>> iter = busiTable.getFieldsIterator();
//        List<String> keyColumnIndexList = new ArrayList<String>();
//        JSONArray ja = new JSONArray();
//        while(iter.hasNext()){
//            Entry<String, BIField> entry = iter.next();
//            String columnName = entry.getOriginalValue();
//            BIField f = entry.getKey();
//            if(f.getType() != fieldType) {
//                continue;
//            }
//            ja.put(asTableJson(columnName, tableName, connectionName, dbLink, schemaName, userId));
//        }
//        JSONObject result = new JSONObject();
//        result.put("fields", ja);
//        if (!keyColumnIndexList.isEmpty()) {
//            result.put("key", asTableJson(keyColumnIndexList.get(0), tableName, connectionName, dbLink, schemaName, userId));
//        }
//        WebUtils.printAsJSON(res, result);
    }


    /**
     * TODO 代码质量 这个到处写 就像随地大小便一样
     *
     * @param columnName
     * @param tableName
     * @param connectionName
     * @param dbLink
     * @return
     * @throws Exception
     */
    private JSONObject asTableJson(String columnName, String tableName, String connectionName, String dbLink, String schema, long userId) throws Exception {
        JSONObject jo = new JSONObject();
//		jo.put("table_name", tableName);
//        jo.put("schema_name", schema);
//		jo.put("dbLink", dbLink);
//		BITableTranslater trans = BIInterfaceAdapter.getBIConnectionAdapter().getTableTranslater(connectionName, null, tableName, userId);
//		jo.put("table_name_text", trans.translateTableName(null, tableName));
//		jo.put("field_name", columnName);
//		jo.put("field_name_text", trans.translateFieldName(columnName));
//		jo.put("connection_name", connectionName);

        return jo;
    }


}