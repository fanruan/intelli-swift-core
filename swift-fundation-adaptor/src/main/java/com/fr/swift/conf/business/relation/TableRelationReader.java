package com.fr.swift.conf.business.relation;

import com.finebi.base.constant.FineEngineType;
import com.finebi.common.internalimp.config.entryinfo.sql.DatabaseEntryInfo;
import com.finebi.common.internalimp.config.relation.visitor.RelationFieldIdUtils;
import com.finebi.common.internalimp.config.relation.visitor.RelationImpl;
import com.finebi.common.internalimp.config.session.CommonConfigManager;
import com.finebi.common.structure.config.entryinfo.EntryInfo;
import com.finebi.common.structure.config.relation.Relation;
import com.finebi.common.structure.config.relation.visitor.RelationFieldId;
import com.fr.data.core.db.DBUtils;
import com.fr.engine.constant.Null;
import com.fr.engine.criterion.entity.DatabaseEntity;
import com.fr.engine.exception.IllegalArgumentEngineException;
import com.fr.engine.exception.SqlEngineException;
import com.fr.engine.utils.EqualsUtils;
import com.fr.engine.utils.StringUtils;
import com.fr.general.ComparatorUtils;
import com.fr.swift.adaptor.encrypt.SwiftEncryption;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.db.ConnectionInfo;
import com.fr.swift.source.db.ConnectionManager;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/4/8
 */
public class TableRelationReader {

    private String packageId;
    private String dbLinkName;
    private String tableName;
    private String schema;
    private String tableId;
    private DatabaseEntryInfo currentEntryInfo;

    private TableRelationReader(String packageId, DatabaseEntryInfo info) {
        this.packageId = packageId;
        this.dbLinkName = info.getDataSourceName();
        this.tableName = info.getDbTableName();
        this.schema = info.getSchema();
        this.tableId = info.getID();
        this.currentEntryInfo = info;
    }

    public static TableRelationReader create(String packageId, EntryInfo entryInfo) {
        if ((StringUtils.isEmpty(packageId)) || (Null.isNull(entryInfo)) || (!(entryInfo instanceof DatabaseEntryInfo))) {
            throw new IllegalArgumentEngineException("illegal table");
        }
        DatabaseEntryInfo dbEntryInfo = (DatabaseEntryInfo) entryInfo;
        return new TableRelationReader(packageId, dbEntryInfo);
    }

    public List<Relation> build() {
        Connection connection = null;
        try {
            ConnectionInfo info = ConnectionManager.getInstance().getConnectionInfo(this.dbLinkName);
            if (null != info) {
                connection = info.getFrConnection().createConnection();
                if (null != connection) {
                    return readRelation(connection, getExistTableMapByPackageIdAndDataLinkName());
                }
            }
            return new ArrayList<Relation>();
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e.getMessage(), e);
            throw new SqlEngineException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    SwiftLoggers.getLogger().error(e.getMessage(), e);
                }
            }
        }
    }

    private List<Relation> readRelation(Connection connection,
                                       List<DatabaseEntryInfo> allTableSources) {
        List<Relation> relationsSet = new ArrayList<Relation>();
        Iterator<DatabaseEntryInfo> sit = allTableSources.iterator();

        getRelationsSet(connection, relationsSet, sit, allTableSources);
        DBUtils.closeConnection(connection);

        return relationsSet;
    }

    private void getRelationsSet(Connection connection, List<Relation> relationsSet, Iterator<DatabaseEntryInfo> sit, List<DatabaseEntryInfo> oldTableSources) {
        getRelationSet(connection, currentEntryInfo, relationsSet, oldTableSources);
        while (sit.hasNext()) {
            DatabaseEntryInfo dbEntry = sit.next();
            getRelationSet(connection, dbEntry, relationsSet, oldTableSources);
        }
    }

    /**
     * 读取出来是 N-1 的所以下面primary和foreign是反的
     * @param connection
     * @param dbEntry
     * @param relationsSet
     * @param oldTableSources
     */
    private void getRelationSet(Connection connection, DatabaseEntryInfo dbEntry, List<Relation> relationsSet, List<DatabaseEntryInfo> oldTableSources) {
        String connectionName = dbEntry.getDataSourceName();

        Map<String, DBTableRelation> currentTableRelationMap = getAllRelationOfConnection(connection, dbEntry.getSchema(), dbEntry.getDbTableName());
        Iterator<Map.Entry<String, DBTableRelation>> rIt = currentTableRelationMap.entrySet().iterator();
        while (rIt.hasNext()) {
            Map.Entry<String, DBTableRelation> currentTableRelation = rIt.next();
            DBTableRelation relation = currentTableRelation.getValue();
            if (ComparatorUtils.equals(this.dbLinkName, connectionName) && ComparatorUtils.equals(relation.getFkTable(), this.tableName)) {
                // 新增表为
                RelationFieldId primaryFieldId = RelationFieldIdUtils.create(relation.getPrimaryColumnName());
                RelationFieldId foreignFieldId = RelationFieldIdUtils.create(relation.getForeignColumnName());
                relationsSet.add(createRelation(dbEntry.getID(), primaryFieldId, this.tableId, foreignFieldId));
            } else if (ComparatorUtils.equals(dbEntry.getID(), tableId)) {
                // 如果当前表和新增表相同则遍历所有表加关联
                for (DatabaseEntryInfo info : oldTableSources) {
                    if (ComparatorUtils.equals(info.getDataSourceName(), connectionName) && ComparatorUtils.equals(relation.getFkTable(), info.getDbTableName())) {
                        RelationFieldId primaryFieldId = RelationFieldIdUtils.create(relation.getPrimaryColumnName());
                        RelationFieldId foreignFieldId = RelationFieldIdUtils.create(relation.getForeignColumnName());
                        relationsSet.add(createRelation(dbEntry.getID(), primaryFieldId, info.getID(), foreignFieldId));
                    }
                }
            }
        }
    }

    /**
     * Relation因为读出来是  N : 1 所以要反过来
     * @param primaryTableId
     * @param primaryFieldId
     * @param foreignTableId
     * @param foreignFieldId
     * @return
     */
    private Relation createRelation(String primaryTableId, RelationFieldId primaryFieldId, String foreignTableId, RelationFieldId foreignFieldId) {
        return new RelationImpl(foreignTableId, foreignFieldId, primaryTableId, primaryFieldId);
    }

    /**
     * 读取外键信息
     * @param conn Sql connection
     * @param schemaName
     * @param tableName
     * @return <外键名称, 关联>
     */
    private Map<String, DBTableRelation> getAllRelationOfConnection(Connection conn, String schemaName, String tableName) {
        Map<String, DBTableRelation> result = new HashMap<String, DBTableRelation>();
        try {
            DatabaseMetaData dbMetaData = conn.getMetaData();
            String catalog = conn.getCatalog();
            ResultSet foreignKeyResultSet;
            if (ComparatorUtils.equals(conn.getMetaData().getDriverName(), "Hive JDBC")) {
                foreignKeyResultSet = conn.getMetaData().getCatalogs();
            } else {
                foreignKeyResultSet = dbMetaData.getExportedKeys(catalog, schemaName, tableName);
            }

            while (foreignKeyResultSet.next()) {
                String fkName = foreignKeyResultSet.getString("FK_NAME");
                String pkColumnName = foreignKeyResultSet.getString("PKCOLUMN_NAME");
                String pkTableName = foreignKeyResultSet.getString("PKTABLE_NAME");
                String pkSchema = foreignKeyResultSet.getString("PKTABLE_SCHEM");
                DbTableColumn primary = new DbTableColumn(pkColumnName, pkTableName, pkSchema);
                String fkColumnName = foreignKeyResultSet.getString("FKCOLUMN_NAME");
                String fkTablenName = foreignKeyResultSet.getString("FKTABLE_NAME");
                String fkSchemaName = foreignKeyResultSet.getString("FKTABLE_SCHEM");
                DbTableColumn foreign = new DbTableColumn(fkColumnName, fkTablenName, fkSchemaName);
                if (!result.containsKey(fkName)) {
                    result.put(fkName, new DBTableRelation(pkTableName, fkTablenName));
                }
                result.get(fkName).addPrimaryColumn(primary);
                result.get(fkName).addForeignColumn(foreign);
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("getRelationError", e);
        }

        return result;
    }

    /**
     * 获取所有的数据库表信息
     * @return
     */
    private List<DatabaseEntryInfo> getExistTableMapByPackageIdAndDataLinkName() {
        List<DatabaseEntryInfo> entryInfos = new ArrayList<DatabaseEntryInfo>();
        for (String tableId : CommonConfigManager.getPackageSession(FineEngineType.Cube).getPackageById(this.packageId).getTableIds()) {
            EntryInfo entryInfo = CommonConfigManager.getEntryInfoSession(FineEngineType.Cube).find(tableId);
            if ((DatabaseEntity.driverType.equals(entryInfo.getType())) &&
                    (EqualsUtils.equals(entryInfo.getDataSourceName(), this.dbLinkName)) &&
                    (!EqualsUtils.equals(entryInfo.getID(), this.tableId))) {
                DatabaseEntryInfo databaseEntryInfo = (DatabaseEntryInfo) entryInfo;
                if (!entryInfos.contains(databaseEntryInfo)) {
                    entryInfos.add(databaseEntryInfo);
                }
            }
        }
        return entryInfos;
    }

    /**
     * 关联字段信息
     */
    private class DbTableColumn {
        private String columnName;
        private String tableName;
        private String schema;

        public DbTableColumn(String columnName, String tableName, String schema) {
            this.columnName = columnName;
            this.tableName = tableName;
            this.schema = schema;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public String getSchema() {
            return schema;
        }

        public void setSchema(String schema) {
            this.schema = schema;
        }
    }

    /**
     * 表关联
     */
    private class DBTableRelation {
        /**
         * 主表字段
         */
        private List<DbTableColumn> primaryColumns;
        /**
         * 子表字段
         */
        private List<DbTableColumn> foreignColumns;
        /**
         * 主表名
         */
        private String pkTable;
        /**
         * 子表名
         */
        private String fkTable;

        public DBTableRelation(String pkTable, String fkTable) {
            primaryColumns = new ArrayList<DbTableColumn>();
            foreignColumns = new ArrayList<DbTableColumn>();
            this.pkTable = pkTable;
            this.fkTable = fkTable;
        }

        public List<DbTableColumn> getPrimaryColumns() {
            return primaryColumns;
        }

        public List<DbTableColumn> getForeignColumns() {
            return foreignColumns;
        }

        public void addPrimaryColumn(DbTableColumn column) {
            primaryColumns.add(column);
        }

        public void addForeignColumn(DbTableColumn column) {
            foreignColumns.add(column);
        }

        public List<String> getPrimaryColumnName() {
            List<String> result = new ArrayList<String>();
            for (DbTableColumn column : primaryColumns) {
                result.add(SwiftEncryption.encryptFieldId(pkTable, column.getColumnName()));
            }
            return result;
        }

        public List<String> getForeignColumnName() {
            List<String> result = new ArrayList<String>();
            for (DbTableColumn column : foreignColumns) {
                result.add(SwiftEncryption.encryptFieldId(fkTable, column.getColumnName()));
            }
            return result;
        }

        public String getPkTable() {
            return pkTable;
        }

        public void setPkTable(String pkTable) {
            this.pkTable = pkTable;
        }

        public String getFkTable() {
            return fkTable;
        }

        public void setFkTable(String fkTable) {
            this.fkTable = fkTable;
        }
    }

}