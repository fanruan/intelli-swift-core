package com.fr.bi.web.conf.utils;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.field.BIBusinessField;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.relation.BITableRelationHelper;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableRelation;
import com.fr.bi.conf.base.datasource.BIConnectionManager;
import com.fr.bi.conf.data.source.DBTableSource;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.data.BIFieldID;
import com.fr.bi.stable.data.db.BIDBTableField;
import com.fr.bi.stable.data.db.IPersistentTable;
import com.fr.bi.stable.data.db.PersistentField;
import com.fr.bi.stable.utils.BIDBUtils;
import com.fr.data.core.db.DBUtils;
import com.fr.file.DatasourceManager;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.sql.Connection;
import java.util.*;

/**
 * Created by Young's on 2016/12/19.
 */
public class BIReadRelationTranslationUtils {

    /**
     * 读关联
     *
     * @param newTableSources
     * @param allTableSources
     * @param allFieldIdMap
     * @return
     */
    public static Set<BITableRelation> readRelation(Map<String, DBTableSource> newTableSources,
                                                    Map<String, DBTableSource> allTableSources,
                                                    Map<String, String> allFieldIdMap) {
        Map<String, Connection> connMap = new HashMap<String, Connection>();
        Map<String, DBTableSource> oldTableSources = new HashMap<String, DBTableSource>(allTableSources);//已经在包内的表
        allTableSources.putAll(newTableSources);
        Set<BITableRelation> relationsSet = new HashSet<BITableRelation>();
        Iterator<Map.Entry<String, DBTableSource>> sit = allTableSources.entrySet().iterator();
        while (sit.hasNext()) {
            Map.Entry<String, DBTableSource> tableID2Table = sit.next();
            DBTableSource currentTable = tableID2Table.getValue();
            String connectionName = currentTable.getDbName();

            if (!connMap.containsKey(connectionName)) {
                com.fr.data.impl.Connection dbc = DatasourceManager.getInstance().getConnection(connectionName);
                if (dbc == null) {
                    continue;
                }
                try {
                    connMap.put(connectionName, dbc.createConnection());
                } catch (Exception e) {
                    BILoggerFactory.getLogger().error(e.getMessage());
                    continue;
                }
            }
            Map<String, Set<BIDBTableField>> currentTableRelationMap = BIDBUtils.getAllRelationOfConnection(connMap.get(connectionName), BIConnectionManager.getInstance().getSchema(currentTable.getDbName()), currentTable.getTableName());
            Iterator<Map.Entry<String, Set<BIDBTableField>>> rIt = currentTableRelationMap.entrySet().iterator();
            while (rIt.hasNext()) {
                Map.Entry<String, Set<BIDBTableField>> currentTableRelation = rIt.next();
                for (BIDBTableField foreignField : currentTableRelation.getValue()) {//读取当前表所有关联
                    Iterator<Map.Entry<String, DBTableSource>> sit1 = newTableSources.entrySet().iterator();
                    while (sit1.hasNext()) {//对于所有新表进行判断
                        Map.Entry<String, DBTableSource> newAddedTableMap = sit1.next();
                        DBTableSource newAddedTable = newAddedTableMap.getValue();
                        if (isTableEqual(newAddedTable, foreignField, connectionName)) {//如果当前表与新表关联,则加入关联
                            relationsSet.add(new BITableRelation(
                                    new BIBusinessField(tableID2Table.getKey(),
                                            currentTableRelation.getKey(), new BIFieldID(allFieldIdMap.get(tableID2Table.getKey() + currentTableRelation.getKey()))),
                                    new BIBusinessField(newAddedTableMap.getKey(),
                                            foreignField.getFieldName(), new BIFieldID(allFieldIdMap.get(newAddedTableMap.getKey() + foreignField.getFieldName())))));
                        } else if (ComparatorUtils.equals(currentTable, newAddedTable)) {//如果当前表与新表不关联,但是当前表与当前新表相同
                            for (Map.Entry<String, DBTableSource> oldEntry : oldTableSources.entrySet()) {//对所有表进行
                                if (isTableEqual(oldEntry.getValue(), foreignField, connectionName)) {
                                    relationsSet.add(new BITableRelation(
                                            new BIBusinessField(newAddedTableMap.getKey(),
                                                    currentTableRelation.getKey(), new BIFieldID(allFieldIdMap.get(newAddedTableMap.getKey() + currentTableRelation.getKey()))),
                                            new BIBusinessField(oldEntry.getKey(),
                                                    foreignField.getFieldName(), new BIFieldID(allFieldIdMap.get(oldEntry.getKey() + foreignField.getFieldName())))));
                                }
                            }
                        }
                    }
                }
            }
        }
        Iterator it = connMap.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            DBUtils.closeConnection(connMap.get(key));
        }
        return relationsSet;
    }

    private static boolean isTableEqual(DBTableSource source, BIDBTableField field, String connName) {
        return ComparatorUtils.equals(source.getDbName(), connName)
                && ComparatorUtils.equals(source.getTableName(), field.getTableName());
    }

    /**
     * 读转义
     *
     * @param sourceTables
     * @param allFieldIdMap
     * @return
     * @throws Exception
     */
    public static JSONObject readTranslation(Map<String, DBTableSource> sourceTables, Map<String, String> allFieldIdMap) throws Exception {
        JSONObject jo = new JSONObject();
        JSONObject tableTrans = new JSONObject();
        JSONObject fieldTrans = new JSONObject();
        jo.put(BIJSONConstant.JSON_KEYS.TABLE, tableTrans);
        jo.put(BIJSONConstant.JSON_KEYS.FIELD, fieldTrans);
        Iterator<Map.Entry<String, DBTableSource>> it = sourceTables.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, DBTableSource> entry = it.next();
            IPersistentTable table = entry.getValue().getPersistentTable();
            if (!StringUtils.isEmpty(table.getRemark())) {
                tableTrans.put(entry.getKey(), table.getRemark());
            }
            for (PersistentField column : table.getFieldList()) {
                if (!StringUtils.isEmpty(column.getRemark())) {
                    fieldTrans.put(allFieldIdMap.get(entry.getKey() + column.getFieldName()), column.getRemark());
                }
            }
        }
        return jo;
    }


    public static void saveRelation(long userId, JSONArray relationJA, String fieldId) throws Exception {
        if (fieldId != null) {
            //删掉原来这个字段上的关联
            BusinessField field = BICubeConfigureCenter.getDataSourceManager().getBusinessField(new BIFieldID(fieldId));
            BusinessTable table = field.getTableBelongTo();
            Set<BITableRelation> primRelations = BICubeConfigureCenter.getTableRelationManager().getPrimaryRelation(userId, table).getContainer();
            Set<BITableRelation> foreignRelations = BICubeConfigureCenter.getTableRelationManager().getForeignRelation(userId, table).getContainer();
            for (BITableRelation relation : primRelations) {
                if (ComparatorUtils.equals(relation.getPrimaryKey().getFieldID().getIdentityValue(), fieldId) ||
                        ComparatorUtils.equals(relation.getForeignKey().getFieldID().getIdentityValue(), fieldId)) {
                    BICubeConfigureCenter.getTableRelationManager().removeTableRelation(userId, relation);
                }
            }
            for (BITableRelation relation : foreignRelations) {
                if (ComparatorUtils.equals(relation.getPrimaryKey().getFieldID().getIdentityValue(), fieldId) ||
                        ComparatorUtils.equals(relation.getForeignKey().getFieldID().getIdentityValue(), fieldId)) {
                    BICubeConfigureCenter.getTableRelationManager().removeTableRelation(userId, relation);
                }
            }
        }
        Set<BITableRelation> relationsSet = new HashSet<BITableRelation>();
        for (int i = 0; i < relationJA.length(); i++) {
            try {
                JSONObject r = relationJA.getJSONObject(i);
                JSONObject pKeyJO = r.getJSONObject("primaryKey");
                JSONObject fKeyJO = r.getJSONObject("foreignKey");
                JSONObject reConstructedRelationJo = new JSONObject();
                JSONObject reConstructedPrimaryKeyJo = new JSONObject();
                JSONObject reConstructedForeignKeyJo = new JSONObject();
                reConstructedPrimaryKeyJo.put("field_id", pKeyJO.getString("field_id"));
                reConstructedForeignKeyJo.put("field_id", fKeyJO.getString("field_id"));
                reConstructedRelationJo.put("primaryKey", reConstructedPrimaryKeyJo);
                reConstructedRelationJo.put("foreignKey", reConstructedForeignKeyJo);
                BITableRelation tableRelation = BITableRelationHelper.getRelation(reConstructedRelationJo);
                relationsSet.add(tableRelation);
            } catch (Exception e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);
            }
        }
        BICubeConfigureCenter.getTableRelationManager().registerTableRelationSet(userId, relationsSet);
    }
}
