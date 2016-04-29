package com.fr.bi.web.conf.services;

import com.fr.bi.web.conf.AbstractBIConfigureAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BISynchronousTableInforInPackage extends AbstractBIConfigureAction {
    @Override
    public String getCMD() {
        return "synchronous_table_infor_in_package";
    }

    /*
     * {
     *     connection_name:"",
     *     package_name:"",
     *     schema_name:"",
     *     table_name:"",
     *     table_name_text:"",
     *     fields:[{
     *         field_name:"", "join_analyse":true, field_name_text:""
     *     }],
     *     group_relation:[],
     *     formula_relation:[]
     *     modified_key_fields:[{foregin_field:{}, key_field:{}},
     *     {foregin_field:{}, key_field:{}}]
     * }
     */

    /**
     * 行为
     *
     * @param req 数据
     * @param res 返回值
     * @throws Exception
     */
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
//		String translatedJsonString = WebUtils.getHTTPRequestParameter(req, "new_table");
//        boolean isAdd = Boolean.valueOf(WebUtils.getHTTPRequestParameter(req, "is_add"));
//        long userId = ServiceUtils.getCurrentUserID(req);
//        JSONObject tableJson = new JSONObject(translatedJsonString);
//
//        BIAbstractBusiTable table = BIConfUtils.getTableByJsonObject(tableJson, userId);
//        if (table == null){
//            BIBusiPack pack = BIInterfaceAdapter.getBIBusiPackAdapter().getFinalVersionOfPackByName(tableJson.getString("package_name"), userId);
//            JSONObject jo = new JSONObject();
//            jo.put("connection_name", tableJson.getString("connection_name"));
//            jo.put("table", tableJson);
//            table = pack.addTableByJSONArrayWithoutGenerateCube(jo, isAdd, userId);
//        } else {
//        	table.parseJSON(tableJson, userId);
//        }
//        tableJson.put("md5_table_name", table.getMd5TableName());
//        BIInterfaceAdapter.getBIConnectionAdapter().saveTableTranslaterByJson(tableJson, table, userId);
//		JSONArray relationArray = tableJson.getJSONArray("modified_relation_array");
//
//		for(int i = 0; i < relationArray.length(); i++) {
//			JSONObject foreignField = relationArray.getJSONObject(i);
//			String foreFieldName = foreignField.getString("field_name");
//            BIFieldKey fck = table.getRealFieldKey(foreFieldName);
//			BIInterfaceAdapter.getBIConnectionAdapter().settingUseClearRelationByForeignField(fck.getDbName(),fck.getSchema(), fck.getTableName(), fck.getDBLink(), foreFieldName, userId);
//
//			if(StringUtils.isBlank(foreignField.getString("primary_key"))) {
//				continue;
//			}
//			JSONArray keyFields = foreignField.getJSONArray("primary_key");
//            for (int j = 0; j < keyFields.length(); j ++){
//                JSONObject keyField = keyFields.getJSONObject(j);
//                String keyConnectionName = keyField.getString("connection_name");
//                String keySchemaName = (keyField.has("schema_name") ? keyField.getString("schema_name") : null);
//                String keyTableName = keyField.getString("table_name");
//                String keyDBLink = keyField.has("dbLink") ? keyField.getString("dbLink") : null;
//                String keyMd5Name = keyField.has("md5_table_name") ? keyField.getString("md5_table_name") : keyTableName;
//
//                String keyFieldName = keyField.getString("field_name");
//                BITableKey pTableKey = new BITableKey(keyConnectionName, keySchemaName, keyMd5Name, keyTableName, keyDBLink);
//                BIAbstractBusiTable pTable = BIConfUtils.getBusiTable4Config(pTableKey, userId);
//                BIFieldKey pky = pTable == null ? new BIFieldKey(pTableKey, keyFieldName) : pTable.getRealFieldKey(keyFieldName);
//                BITableFieldRelation relation = null;
//                if(!StringUtils.isBlank(keyFieldName)) {
//                    relation = 	new BITableFieldRelation(
//                            pky.getDbName(),
//                            pky.getSchema(),
//                            pky.getTableName(),
//                            pky.getShowName(),
//                            pky.getDBLink(),
//                            keyFieldName,
//
//                            fck.getDbName(),
//                            fck.getSchema(),
//                            fck.getTableName(),
//                            fck.getShowName(),
//                            fck.getDBLink(),
//                            foreFieldName
//                    );
//                    BIInterfaceAdapter.getBIConnectionAdapter().settingUseAddTableFieldRelation(relation, userId);
//                }
//
//            }
//		}
//        if (table != null) {
//			//TODO 代码质量 重复计算了
//			table.synchronousFieldsUsability(tableJson.getJSONArray("fields"), tableJson.getString("connection_name"));
//		}
//        if (ComparatorUtils.equals(BIBaseConstant.CUBEINDEX.EXCEL_CONNECTION, table.getConnectionName())){
//            BIInterfaceAdapter.getCubeAdapter().addTask(new SingleTableTask( new BITableKey(table.getConnectionName(), null, table.getMd5TableName(), table.getName(), null), userId), userId);
//        }
//
//		FRContext.getCurrentEnv().writeResource(BIInterfaceAdapter.getBIConnectionAdapter().getBIConnectionManager(userId));
//		FRContext.getCurrentEnv().writeResource(BIInterfaceAdapter.getBIBusiPackAdapter().getBusiPackageManager(userId));
    }
}