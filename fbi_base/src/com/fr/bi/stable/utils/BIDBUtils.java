package com.fr.bi.stable.utils;

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.base.FRContext;
import com.fr.base.TableData;
import com.fr.bi.conf.base.datasource.BIConnectOptimizationUtils;
import com.fr.bi.conf.base.datasource.BIConnectOptimizationUtilsFactory;
import com.fr.bi.conf.base.datasource.BIConnectionManager;
import com.fr.bi.manager.DBCPConnectionPlugInterface;
import com.fr.bi.manager.DBCPConnectionPlugManager;
import com.fr.bi.manager.PerformancePlugManager;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.BIDBTableField;
import com.fr.bi.stable.data.db.DistinctColumnSelect;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.db.PersistentField;
import com.fr.bi.stable.data.db.PersistentTable;
import com.fr.bi.stable.data.db.SQLStatement;
import com.fr.bi.stable.data.db.ServerLinkInformation;
import com.fr.bi.stable.data.db.SqlSettedStatement;
import com.fr.bi.stable.dbdealer.DBDealer;
import com.fr.bi.stable.dbdealer.DateDealer;
import com.fr.bi.stable.dbdealer.DoubleDealer;
import com.fr.bi.stable.dbdealer.LongDealer;
import com.fr.bi.stable.dbdealer.StringDealer;
import com.fr.bi.stable.dbdealer.StringDealerWithCharSet;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.data.core.db.ColumnInformation;
import com.fr.data.core.db.DBUtils;
import com.fr.data.core.db.dialect.Dialect;
import com.fr.data.core.db.dialect.DialectFactory;
import com.fr.data.core.db.dialect.OracleDialect;
import com.fr.data.core.db.dml.Table;
import com.fr.data.core.db.field.FieldMessage;
import com.fr.data.impl.DBTableData;
import com.fr.data.impl.EmbeddedTableData;
import com.fr.data.impl.JDBCDatabaseConnection;
import com.fr.data.pool.DBCPConnectionPoolAttr;
import com.fr.file.DatasourceManager;
import com.fr.file.DatasourceManagerProvider;
import com.fr.general.ComparatorUtils;
import com.fr.general.data.DataModel;
import com.fr.json.JSONObject;
import com.fr.script.Calculator;
import com.fr.stable.StringUtils;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by GUY on 2015/3/3.
 */

/**
 * 数据库操作
 */
public class BIDBUtils {
    private static BILogger LOGGER = BILoggerFactory.getLogger();
    private static int INIT_TIME_BETWEEN_EVICTION_RUNS_MILLIS = -1;
    private static int BI_TIME_BETWEEN_EVICTION_RUNS_MILLI = 500000;
    private static int INIT_MIN_EVICTABLEIDLE_TIME_MILLIS = 1800000;
    private static int BI_MIN_EVICTABLEIDLE_TIME_MILLIS = 300000;
    private final static int MAX_LONG_COLUMN_SIZE = 20;

    /**
     * 给数据库列设置初始的类型
     *
     * @param biType bi类型
     * @return 数据库类型
     * 该方法现在弃用
     */
    @Deprecated
    public static int biTypeToSql(int biType) {
        switch (biType) {
            case DBConstant.COLUMN.NUMBER:
                return java.sql.Types.DOUBLE;

            case DBConstant.COLUMN.DATE:
                return java.sql.Types.DATE;

            default:
                return java.sql.Types.VARCHAR;

        }
    }

    public static int classTypeToSql(int classType) {
        switch (classType) {
            case DBConstant.CLASS.INTEGER: {
                return Types.INTEGER;
            }
            case DBConstant.CLASS.LONG: {
                return Types.BIGINT;
            }
            case DBConstant.CLASS.FLOAT: {
                return Types.FLOAT;
            }
            case DBConstant.CLASS.DOUBLE: {
                return Types.DOUBLE;
            }
            case DBConstant.CLASS.DECIMAL: {
                return Types.DOUBLE;
            }
            case DBConstant.CLASS.DATE:
                return Types.DATE;
            case DBConstant.CLASS.TIMESTAMP:
                return Types.TIMESTAMP;
            case DBConstant.CLASS.TIME:
                return Types.TIME;
            case DBConstant.CLASS.ROW:
                return Types.INTEGER;
            default: {
                return Types.VARCHAR;
            }
        }

    }

    /**
     * 通过java类判断字段类型
     *
     * @param classType java类型
     * @return bi类型
     */
    public static int checkColumnTypeFromClass(int classType) {
        switch (classType) {
            case DBConstant.CLASS.DECIMAL:
                return DBConstant.COLUMN.NUMBER;

            case DBConstant.CLASS.INTEGER:
                return DBConstant.COLUMN.NUMBER;
            case DBConstant.CLASS.LONG:
                return DBConstant.COLUMN.NUMBER;
            case DBConstant.CLASS.FLOAT:
                return DBConstant.COLUMN.NUMBER;
            case DBConstant.CLASS.DOUBLE:
                return DBConstant.COLUMN.NUMBER;

            case DBConstant.CLASS.DATE:
                return DBConstant.COLUMN.DATE;
            case DBConstant.CLASS.TIMESTAMP:
                return DBConstant.COLUMN.DATE;
            case DBConstant.CLASS.TIME:
                return DBConstant.COLUMN.DATE;
            case DBConstant.CLASS.ROW:
                return DBConstant.COLUMN.ROW;
            default:
                return DBConstant.COLUMN.STRING;

        }
    }

    /**
     * 通过数据库类型判断java类
     *
     * @param sqlType     数据库类型
     * @param scale
     * @param column_size
     * @return java类型
     */
    public static int checkColumnClassTypeFromSQL(int sqlType, int column_size, int scale) {
        switch (sqlType) {
            case java.sql.Types.DECIMAL:
                return getDEecimalType(column_size, scale);
            case java.sql.Types.NUMERIC:
                return getDEecimalType(column_size, scale);
            case java.sql.Types.BOOLEAN:
                return DBConstant.CLASS.BOOLEAN;
            case java.sql.Types.BIT:
                return DBConstant.CLASS.INTEGER;
            case java.sql.Types.TINYINT:
                return DBConstant.CLASS.INTEGER;
            case java.sql.Types.SMALLINT:
                return DBConstant.CLASS.INTEGER;
            case java.sql.Types.INTEGER:
                return DBConstant.CLASS.INTEGER;
            case java.sql.Types.BIGINT:
                return DBConstant.CLASS.LONG;
            case java.sql.Types.REAL:
                return getDoubleType(column_size, scale);
            case java.sql.Types.DOUBLE:
                return getDoubleType(column_size, scale);
            case java.sql.Types.FLOAT:
                return getDoubleType(column_size, scale);
            case java.sql.Types.DATE:
                return DBConstant.CLASS.DATE;
            case java.sql.Types.TIMESTAMP:
                return DBConstant.CLASS.TIMESTAMP;
            case java.sql.Types.TIME:
                return DBConstant.CLASS.TIME;
            default:
                return DBConstant.CLASS.STRING;
        }
    }

    private static int getDEecimalType(int column_size, int scale) {
        if (scale == 0) {
            return getTypeByColumn_size(column_size);
        } else {
            return DBConstant.CLASS.DECIMAL;
        }
    }

    private static int getDoubleType(int column_size, int scale) {
        if (scale == 0) {
            return getTypeByColumn_size(column_size);
        } else {
            return DBConstant.CLASS.DOUBLE;
        }
    }

    /**
     * 19个长度的整形读成long值 以上的读成字符串
     *
     * @param column_size
     * @return
     */
    private static int getTypeByColumn_size(int column_size) {
        if (column_size < MAX_LONG_COLUMN_SIZE || PerformancePlugManager.getInstance().isUseNumberType()) {
            return DBConstant.CLASS.LONG;
        } else {
            return DBConstant.CLASS.STRING;
        }
    }

    /**
     * 通过数据库类型判断字段类型
     *
     * @param sqlType 数据库类型
     * @return bi类型
     */
    public static int sqlType2BI(int sqlType, int column_size, int scale) {
        return checkColumnTypeFromClass(checkColumnClassTypeFromSQL(sqlType, column_size, scale));
    }

    /**
     * 数据集这边简单判断下类型吻合
     *
     * @param value 值
     * @return 类型吻合
     */
    public static int resloveValue(Object value) {
        if (value instanceof String) {
            return java.sql.Types.VARCHAR;
        } else if (value instanceof Date || value instanceof java.sql.Date) {
            return java.sql.Types.DATE;
        } else if (value instanceof Boolean) {
            return java.sql.Types.BOOLEAN;
        }
        //所有NUMBER类型全部转换成DOUBLE
        else if (value instanceof Number) {
            return java.sql.Types.DOUBLE;
        } else {
            return java.sql.Types.BINARY;
        }
    }

    private static PersistentTable getServerBITable(DBTableData tableData, PersistentTable table) {
        String query = tableData.getQuery();
        com.fr.data.impl.Connection connection = tableData.getDatabase();
        java.sql.Connection conn = null;
        if (StringUtils.isNotEmpty(query)) {
            try {
                conn = connection.createConnection();
                Dialect dialect = DialectFactory.generateDialect(conn, connection.getDriver());
//                ColumnInformation[] columns = com.fr.data.core.db.DBUtils.checkInColumnInformation(conn, dialect, query);

                ColumnInformation[] columns = checkColumnInfo(conn, dialect, query);
                for (int i = 0, cols = columns.length; i < cols; i++) {
                    int columnSize = columns[i].getColumnSize();
                    PersistentField column;
                    if (columnSize == 0) {
                        column = new PersistentField(columns[i].getColumnName(), columns[i].getColumnType(), columns[i].getColumnSize());
                    } else {
                        column = convert4Scale(columns[i]);
                    }
                    table.addColumn(column);
                }
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl(e);
            } finally {
                com.fr.data.core.db.DBUtils.closeConnection(conn);
            }
        }
        return table;
    }

    /**
     * Author：Connery
     * 这么多个数据库，就一个处理的类,还是静态的
     * 这个类型转换有很多问题。一个数据库处理好了，完全可能改坏其他的。
     * TODO 每个数据库，各自的处理逻辑
     *
     * @param columnInformation
     * @return
     */

    private static PersistentField convert4Scale(ColumnInformation columnInformation) {

        if (columnInformation.getColumnType() == java.sql.Types.DOUBLE && columnInformation.getScale() == 0) {
            /**
             * Author：Connery
             * 这个IF判断是处理SQLServer2008，读取float类型，没有scale，而导致最终被认为是整型。
             *
             * 这个scale的默认值，我记得是改过的。
             */
            return new PersistentField(
                    columnInformation.getColumnName(),
                    null,
                    columnInformation.getColumnType(),
                    columnInformation.getColumnSize(),
                    PersistentField.DEFAULT_SCALE);
        } else {
            return new PersistentField(
                    columnInformation.getColumnName(),
                    null,
                    columnInformation.getColumnType(),
                    columnInformation.getColumnSize(),
                    columnInformation.getScale());
        }

    }

    private static TableData getServerTableData(String sqlConnection, String sql) {
        try {
            ServerLinkInformation serverLinkInformation = new ServerLinkInformation(sqlConnection, sql);
            return serverLinkInformation.createDBTableData();
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        return null;
    }

    private static PersistentTable getBITableOnlyByTableData(TableData tableData, PersistentTable persistentTable, String tableName) {

        DataModel dm = null;
        try {
            //    dm = tableData.createDataModel(Calculator.createCalculator(), tableName);
            /**
             * Modifier: Yee
             * Modify date: 2017-06-05
             * mongodb插件创建DataModel时的createDataModel(Calculator c)方法调用的是取所有数据的方法。
             * 使用tableData.createDataModel(Calculator.createCalculator(), tableName)调用createDataModel(Calculator c)
             *   创建DataModel时，mongodb插件取的是所有数据所以需要取出所有数据后才能获取字段。
             * 使用tableData.createDataModel(Calculator.createCalculator(), TableData.RESULT_NOT_NEED);
             *   创建DataModel时mongodb插件只取一条数据，在数据量大的情况下能有效提高效率
             * 尝试修改createDataModel(Calculator c)方法，无法判断是在查询数据还是在取字段。
             */
            dm = tableData.createDataModel(Calculator.createCalculator(), TableData.RESULT_NOT_NEED);
            int cols = dm.getColumnCount();
            JSONObject jo = new JSONObject();
            jo.put("tableName", tableName);
            int rowcount = dm.getRowCount();
            for (int i = 0; i < cols; i++) {
                PersistentField column = new PersistentField(dm.getColumnName(i), null, rowcount == 0 ? java.sql.Types.VARCHAR : resloveValue(dm.getValueAt(0, i)), 255, 15);
                persistentTable.addColumn(column);
            }
            return persistentTable;
        } catch (Exception e) {
            FRContext.getLogger().error(e.getMessage(), e);
        } finally {
            if (dm != null) {
                try {
                    dm.release();
                } catch (Exception e) {
                    FRContext.getLogger().error(e.getMessage(), e);
                }
            }
        }

        return persistentTable;
    }

    public static Map<String, Set<BIDBTableField>> getAllRelationOfConnection(Connection conn, String schemaName, String tableName) {
        Map<String, Set<BIDBTableField>> result = new HashMap<String, Set<BIDBTableField>>();
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
                String pkColumnName = foreignKeyResultSet.getString("PKCOLUMN_NAME");

                String fkColumnName = foreignKeyResultSet.getString("FKCOLUMN_NAME");
                String fkTablenName = foreignKeyResultSet.getString("FKTABLE_NAME");
                String fkSchemaName = foreignKeyResultSet.getString("FKTABLE_SCHEM");
                //FIXME 读取的关联怎么存呢
                if (!result.containsKey(pkColumnName)) {
                    result.put(pkColumnName, new HashSet<BIDBTableField>());
                }
                result.get(pkColumnName).add(new BIDBTableField(fkTablenName, fkSchemaName, fkColumnName));
            }
        } catch (Exception e) {
            FRContext.getLogger().error(e.getMessage(), e);
        }

        return result;
    }

    public static PersistentTable getDBTable(com.fr.data.impl.Connection connection, Connection conn, String schema, String table) throws Exception {
        Dialect dialect = DialectFactory.generateDialect(conn, connection.getDriver());
        String translatedTableName = getTransferColumnComment(connection, dialect.getTableCommentName(conn, table, schema, null));
        PersistentTable dbTable = new PersistentTable(schema, table, translatedTableName);

        List<FieldMessage> messages = dialect.getTableFieldsMessage(conn, table, schema, null);
        for (FieldMessage message : messages) {
            String comment = getTransferColumnComment(connection, message.getColumnComment());
            message.setColumnComment(comment);
            int columnType = message.getColumnType();
            if (columnType == Types.OTHER && dialect instanceof OracleDialect) {
                message.setColumnType(recheckOracleColumnType(conn, message.getColumnName(), table, columnType));
            }
            if (!(dialect instanceof OracleDialect)) {
                if (columnType == DBConstant.CLASS.DECIMAL) {
                    message.setColumnDecimalDigits(PersistentField.DEFAULT_SCALE);
                }
            }
            dbTable.addColumn(new PersistentField(message.getColumnName(), message.getColumnComment(), columnType, message.isPrimaryKey(), message.getColumnSize(), message.getColumnDecimalDigits()));
        }
        return dbTable;
    }

    public static String getColumnNameText(com.fr.data.impl.Connection connection, Map item) throws UnsupportedEncodingException {
        String columnNameText = (String) item.get("column_comment");
        return getTransferColumnComment(connection, columnNameText);
    }

    public static String getTransferColumnComment(com.fr.data.impl.Connection connection, String originalCodeText) throws UnsupportedEncodingException {
        String originalCharsetName = connection.getOriginalCharsetName();
        String newCharsetName = connection.getNewCharsetName();
        boolean needCharSetConvert = StringUtils.isNotBlank(originalCharsetName)
                && StringUtils.isNotBlank(newCharsetName);
        if (needCharSetConvert && originalCodeText != null) {
            originalCodeText = new String(originalCodeText.getBytes(originalCharsetName), newCharsetName);
        }
        return originalCodeText;
    }

    //万恶的oracle万恶的timestamp长度
    private static int recheckOracleColumnType(Connection connection, String columnName, String tableName, int colType) {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery("select DATA_TYPE FROM all_tab_columns where tableName =" + "'" + tableName + "'" + "and COLUMN_NAME =" + "'" + columnName + "'");
            while (rs.next()) {
                if (rs.getString("DATA_TYPE").toUpperCase().contains("TIMESTAMP")) {
                    return Types.TIMESTAMP;
                }
            }
        } catch (Exception e) {

        } finally {
            DBUtils.closeStatement(stmt);
            DBUtils.closeResultSet(rs);
        }
        return colType;
    }


    public static PersistentTable getDBTable(String dbName, String tableName) {
        com.fr.data.impl.Connection connection = BIConnectionManager.getBIConnectionManager().getConnection(dbName);
        String schema = BIConnectionManager.getBIConnectionManager().getSchema(dbName);
        Connection conn = null;
        try {
            conn = connection.createConnection();
            return getDBTable(connection, conn, schema, tableName);
        } catch (Exception e) {
            BILoggerFactory.getLogger(BIDBUtils.class).error(e.getMessage(), e);
        } finally {
            com.fr.data.core.db.DBUtils.closeConnection(conn);
        }
        return null;
    }

    /**
     * 获取服务器数据集的所有字段以及类型
     *
     * @param tableName 服务器数据集名称
     * @return BITable
     * @throws Exception
     */
    public static PersistentTable getServerBITable(String tableName) {
        if (StringUtils.isNotBlank(tableName)) {
            PersistentTable persistentTable = new PersistentTable(null, tableName, null);
            DatasourceManagerProvider datasourceManager = DatasourceManager.getProviderInstance();
            TableData tableData = datasourceManager.getTableData(tableName);
            if (tableData == null) {
                BILoggerFactory.getLogger().error("can not find server db :" + tableName);
            }
            if (tableData instanceof DBTableData) {
                return getServerBITable((DBTableData) tableData, persistentTable);
            } else if (tableData != null) {
                return getBITableOnlyByTableData(tableData, persistentTable, tableName);
            }
        }
        return null;
    }

    /**
     * 从sql语句中获取BITable
     *
     * @param tableName 服务器数据集名称
     * @return BITable
     * @throws Exception
     */
    public static PersistentTable getServerBITable(String connection, String sql, String tableName) {
        TableData tableData = getServerTableData(connection, sql);
        LOGGER.info("The table Data class is: " + tableData.getClass());
        if (StringUtils.isNotBlank(tableName)) {
            PersistentTable persistentTable = new PersistentTable(null, tableName, null);
            if (tableData instanceof DBTableData) {
                LOGGER.info("get server biTable of DBTableData");
                return getServerBITable((DBTableData) tableData, persistentTable);
            } else if (tableData != null) {
                LOGGER.info("get server biTable of OtherTableData");
                return getBITableOnlyByTableData(tableData, persistentTable, tableName);
            }
        }
        return null;
    }


    /**
     * 统一放到runSql里面释放connection，减少创建次数，不能单独使用
     *
     * @param dbName
     * @param tableName
     * @return
     */
    public static SQLStatement getSQLStatement(String dbName, String tableName) {
        com.fr.data.impl.Connection connection = BIConnectionManager.getBIConnectionManager().getConnection(dbName);
        if (connection instanceof JDBCDatabaseConnection) {
            BIConnectOptimizationUtils utils = BIConnectOptimizationUtilsFactory.getOptimizationUtils((JDBCDatabaseConnection) (connection));
            connection = utils.optimizeConnection((JDBCDatabaseConnection) (connection));
        }
        SQLStatement sql = new SQLStatement(connection);
        try {
            Connection conn = sql.getSqlConn();
            Dialect dialect = DialectFactory.generateDialect(conn, connection.getDriver());
            Table table = new Table(BIConnectionManager.getBIConnectionManager().getSchema(dbName), tableName);
            sql.setFrom(dialect.table2SQL(table));
            sql.setSchema(table.getSchema());
            sql.setTableName(table.getName());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return sql;
    }

    public static SQLStatement getSQLStatement(com.fr.data.impl.Connection frConnection, String schema, String tableName) {
        com.fr.data.impl.Connection connection = frConnection;
        if (connection instanceof JDBCDatabaseConnection) {
            BIConnectOptimizationUtils utils = BIConnectOptimizationUtilsFactory.getOptimizationUtils((JDBCDatabaseConnection) (connection));
            connection = utils.optimizeConnection((JDBCDatabaseConnection) (connection));
        }
        SQLStatement sql = new SQLStatement(connection);
        try {
            Connection conn = sql.getSqlConn();
            Dialect dialect = DialectFactory.generateDialect(conn, connection.getDriver());
            Table table = new Table(schema, tableName);
            sql.setFrom(dialect.table2SQL(table));
            sql.setSchema(table.getSchema());
            sql.setTableName(table.getName());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return sql;
    }

    public static SQLStatement getSQLStatementByConditions(String dbName, String tableName, String where) {
        com.fr.data.impl.Connection connection = BIConnectionManager.getBIConnectionManager().getConnection(dbName);
        SQLStatement sql = new SQLStatement(connection);
        try {
            Connection conn = sql.getSqlConn();
            Dialect dialect = DialectFactory.generateDialect(conn, connection.getDriver());
            Table table = new Table(BIConnectionManager.getBIConnectionManager().getSchema(dbName), tableName);
            sql.setFrom(dialect.table2SQL(table));
            sql.setWhere(where);

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return sql;
    }

    public static SQLStatement getDistinctSQLStatement(String dbName, String tableName, String fieldName) {
        com.fr.data.impl.Connection connection = BIConnectionManager.getBIConnectionManager().getConnection(dbName);
        SqlSettedStatement sql = new SqlSettedStatement(connection);
        try {
            Connection conn = sql.getSqlConn();
            Dialect dialect = DialectFactory.generateDialect(conn, connection.getDriver());
            Table table = new Table(BIConnectionManager.getBIConnectionManager().getSchema(dbName), tableName);
            DistinctColumnSelect select = new DistinctColumnSelect(table, fieldName, dialect);
            sql.setSql(select.toSQL());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return sql;
    }

    public static SQLStatement getDistinctSQLStatement(com.fr.data.impl.Connection connection, String schema, String tableName, String fieldName) {
        SqlSettedStatement sql = new SqlSettedStatement(connection);
        try {
            Connection conn = sql.getSqlConn();
            Dialect dialect = DialectFactory.generateDialect(conn, connection.getDriver());
            Table table = new Table(schema, tableName);
            DistinctColumnSelect select = new DistinctColumnSelect(table, fieldName, dialect);
            sql.setSql(select.toSQL());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return sql;
    }

    public static String createSqlString(Dialect dialect, ICubeFieldSource[] columns) {
        StringBuffer sb = new StringBuffer();
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].isUsable()) {
                list.add(dialect.column2SQL(columns[i].getFieldName()));
            }
        }
        for (int i = 0; i < list.size(); i++) {
            if (i != 0) {
                sb.append(",");
            }
            sb.append(list.get(i));
        }
        return sb.toString();
    }

    private static Statement createStatement(Connection conn, Dialect dialect) throws SQLException {
        Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        try {
            stmt.setFetchSize(dialect.getFetchSize());
        } catch (Exception e) {
        }
        return stmt;
    }

    @SuppressWarnings("rawtypes")
    private static DBDealer[] createDBDealer(boolean needCharSetConvert, String originalCharSetName,
                                             String newCharSetName, ICubeFieldSource[] columns) {
        List<DBDealer> res = new ArrayList<DBDealer>();
        for (int i = 0, ilen = columns.length; i < ilen; i++) {
            ICubeFieldSource field = columns[i];
            if (field.isUsable()) {
                DBDealer object = null;
                int rsColumn = i + 1;
                switch (field.getFieldType()) {
                    case DBConstant.COLUMN.DATE: {
                        object = new DateDealer(rsColumn);
                        break;
                    }
                    case DBConstant.COLUMN.NUMBER: {
                        switch (field.getClassType()) {
                            case DBConstant.CLASS.INTEGER:
                            case DBConstant.CLASS.LONG: {
                                object = new LongDealer(rsColumn);
                                break;
                            }
                            case DBConstant.CLASS.DOUBLE:
                            default: {
                                object = new DoubleDealer(rsColumn);
                            }
                        }
                        break;
                    }
                    case DBConstant.COLUMN.STRING:
                    default: {
                        if (needCharSetConvert) {
                            object = new StringDealerWithCharSet(rsColumn, originalCharSetName, newCharSetName);
                        } else {
                            object = new StringDealer(rsColumn);
                        }
                    }
                }
                res.add(object);
            }
        }
        return res.toArray(new DBDealer[res.size()]);
    }


    public static void dealWithJDBCConnection(JDBCDatabaseConnection jdbcDatabaseConnection) {
        if (jdbcDatabaseConnection == null) {
            return;
        }
        DBCPConnectionPoolAttr attr = jdbcDatabaseConnection.getDbcpAttr();
        if (attr == null) {
            attr = new DBCPConnectionPoolAttr();
            jdbcDatabaseConnection.setDbcpAttr(attr);
        }
        //BI-4806处理
        //testOnBorrow不赋值。。
        // 其余两项判断为fr初始值时才做bi需要的初始化，否则不赋值。。供配置可修改。
        if (attr.getTimeBetweenEvictionRunsMillis() == INIT_TIME_BETWEEN_EVICTION_RUNS_MILLIS) {
            attr.setTimeBetweenEvictionRunsMillis(BI_TIME_BETWEEN_EVICTION_RUNS_MILLI);
        }
        if (attr.getMinEvictableIdleTimeMillis() == INIT_MIN_EVICTABLEIDLE_TIME_MILLIS) {
            attr.setMinEvictableIdleTimeMillis(BI_MIN_EVICTABLEIDLE_TIME_MILLIS);
        }
//        attr.setTestOnBorrow(false);

        setDBCPPlugProperties(jdbcDatabaseConnection);
    }

    private static void setDBCPPlugProperties(JDBCDatabaseConnection jdbcDatabaseConnection) {
        DBCPConnectionPlugInterface dbcpConnectionPlug = new DBCPConnectionPlugManager();
        DBCPConnectionPoolAttr att = jdbcDatabaseConnection.getDbcpAttr();

        Set<String> drivers = dbcpConnectionPlug.getDriversNotTestOnBorrow();
        if (drivers.contains(jdbcDatabaseConnection.getDriver())) {
            att.setTestOnBorrow(false);
        }

    }

    public static TableData createTableData(List<String> fields, DataModel dataModel) throws Exception {
        EmbeddedTableData td = new EmbeddedTableData();
        Map<String, Integer> columnIndexMap = new HashMap<String, Integer>();
        for (int col = 0; col < dataModel.getColumnCount(); col++) {
            String name = dataModel.getColumnName(col);
            if (fields.contains(name)) {
                columnIndexMap.put(name, col);
            }
        }
        if (columnIndexMap.size() != fields.size()) {
            return null;
        }
        Map<String, Integer> sortedCols = new LinkedHashMap<String, Integer>();
        for (String s : fields) {
            sortedCols.put(s, columnIndexMap.get(s));
        }
        for (Map.Entry<String, Integer> entry : sortedCols.entrySet()) {
            td.addColumn(entry.getKey(), dataModel.getValueAt(0, entry.getValue()).getClass());
        }
        for (int row = 0; row < dataModel.getRowCount(); row++) {
            List<Object> rowList = new ArrayList<Object>();
            for (Integer col : sortedCols.values()) {
                rowList.add(dataModel.getValueAt(row, col));
            }
            td.addRow(rowList);
        }
        return td;
    }

    public static ColumnInformation[] checkColumnInfo(Connection connection, Dialect dialect, String query) throws SQLException {
        if (connection == null) {
            throw new SQLException("Cannot connect to database!");
        } else {
            connection.setAutoCommit(false);
            Statement currentStatement = null;
            ResultSet currentResultSet = null;

            try {
                if (DBUtils.isProcedure(query)) {
                    Object[] statementAndResultSet = dialect.remoteProcedureCall(connection, query);
                    currentStatement = (Statement) statementAndResultSet[0];
                    currentResultSet = (ResultSet) statementAndResultSet[1];
                } else {
                    currentStatement = dialect.createStatement(connection, query);
//                    currentStatement = connection.createStatement();
                    try {
                        currentResultSet = currentStatement.executeQuery(dialect.createSQL4Columns(query));
                    } catch (Exception var6) {
                        currentStatement.close();
                        currentStatement = connection.createStatement();
                        currentResultSet = currentStatement.executeQuery(query);
                    }
                }
            } catch (SQLException var7) {
                if (currentResultSet != null) {
                    currentResultSet.close();
                }

                if (currentStatement != null) {
                    currentStatement.close();
                }

                throw var7;
            }

            ColumnInformation[] columns = DBUtils.checkInColumnInformationByMetaData(currentResultSet.getMetaData());
            currentResultSet.close();
            currentStatement.close();
            return columns;
        }
    }
}
