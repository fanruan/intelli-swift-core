package com.fr.bi.web.conf.services;

import com.finebi.cube.conf.field.BIBusinessField;
import com.finebi.cube.relation.BITableRelation;
import com.fr.bi.conf.base.datasource.BIConnectionManager;
import com.fr.bi.conf.data.source.DBTableSource;
import com.fr.bi.conf.data.source.MongoDBTableSource;
import com.fr.bi.stable.data.BIFieldID;
import com.fr.bi.stable.data.db.BIDBTableField;
import com.fr.bi.stable.data.source.AbstractTableSource;
import com.fr.bi.web.conf.utils.BIImportDBTableConnectionRelationTool;
import com.fr.data.core.db.DBUtils;
import com.fr.general.ComparatorUtils;

import java.sql.Connection;
import java.util.*;

/**
 * Created by naleite on 16/3/2.
 */
public class BIImportDBTableConnectionExecutor {


    private BIImportDBTableConnectionRelationTool tool = new BIImportDBTableConnectionRelationTool();


    protected Set<BITableRelation> getRelationsByTables(Map<String, AbstractTableSource> newTableSources,
                                                        Map<String, AbstractTableSource> allTableSources,
                                                        Map<String, String> allFieldIdMap, long userId) throws Exception {
        Map<String, Connection> connMap = new HashMap<String, Connection>();
        Map<String, AbstractTableSource> oldTableSources = new HashMap<String, AbstractTableSource>(allTableSources);//已经在包内的表
        allTableSources.putAll(newTableSources);
        //读取范围：当前业务包
//        allTableSources.putAll(tool.getAllBusinessPackDBSourceMap(userId));
        Set<BITableRelation> relationsSet = new HashSet<BITableRelation>();
        Iterator<Map.Entry<String, AbstractTableSource>> sit = allTableSources.entrySet().iterator();
        while (sit.hasNext()) {
            Map.Entry<String, AbstractTableSource> tableID2Table = sit.next();
            AbstractTableSource table = tableID2Table.getValue();
            if(table instanceof MongoDBTableSource){
                continue;
            }
            DBTableSource currentTable = (DBTableSource) table;
            String connectionName = currentTable.getDbName();
            if (!(tool.putConnection(connectionName, connMap))) {
                continue;
            }
            Map<String, Set<BIDBTableField>> currentTableRelationMap = tool.getAllRelationOfConnection(connMap.get(connectionName), BIConnectionManager.getBIConnectionManager().getSchema(currentTable.getDbName()), currentTable.getTableName());
            Iterator<Map.Entry<String, Set<BIDBTableField>>> rIt = currentTableRelationMap.entrySet().iterator();
            while (rIt.hasNext()) {
                Map.Entry<String, Set<BIDBTableField>> currentTableRelation = rIt.next();
                for (BIDBTableField foreignField : currentTableRelation.getValue()) {//读取当前表所有关联
                    Iterator<Map.Entry<String, AbstractTableSource>> sit1 = newTableSources.entrySet().iterator();
                    while (sit1.hasNext()) {//对于所有新表进行判断
                        Map.Entry<String, AbstractTableSource> newAddedTableMap = sit1.next();
                        AbstractTableSource newAddedTable = newAddedTableMap.getValue();
                        if (isEqual(newAddedTable, foreignField, connectionName)) {//如果当前表与新表关联,则加入关联
                            relationsSet.add(new BITableRelation(
                                    new BIBusinessField(tableID2Table.getKey(),
                                            currentTableRelation.getKey(), new BIFieldID(allFieldIdMap.get(tableID2Table.getKey() + currentTableRelation.getKey()))),
                                    new BIBusinessField(newAddedTableMap.getKey(),
                                            foreignField.getFieldName(), new BIFieldID(allFieldIdMap.get(newAddedTableMap.getKey() + foreignField.getFieldName())))));
                        } else if (ComparatorUtils.equals(currentTable, newAddedTable)) {//如果当前表与新表不关联,但是当前表与当前新表相同
                            for (Map.Entry<String, AbstractTableSource> oldEntry : oldTableSources.entrySet()) {//对所有表进行
                                if (isEqual(oldEntry.getValue(), foreignField, connectionName)) {
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


    private boolean isEqual(AbstractTableSource source, BIDBTableField field, String connName) {
        return ComparatorUtils.equals(((DBTableSource) source).getDbName(), connName)
                && ComparatorUtils.equals(source.getTableName(), field.getTableName());
    }
}
