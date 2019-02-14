package com.fr.swift.source.db;

import com.fr.base.TableData;
import com.fr.data.core.db.ColumnInformation;
import com.fr.data.core.db.DBUtils;
import com.fr.data.core.db.dialect.Dialect;
import com.fr.data.core.db.dialect.DialectFactory;
import com.fr.data.core.db.field.FieldMessage;
import com.fr.data.impl.Connection;
import com.fr.data.impl.DBTableData;
import com.fr.file.DatasourceManager;
import com.fr.file.DatasourceManagerProvider;
import com.fr.general.data.DataModel;
import com.fr.script.Calculator;
import com.fr.stable.StringUtils;
import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.setting.PerformancePlugManager;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.retry.RetryLoop;
import com.fr.swift.source.retry.RetryNTimes;
import com.fr.swift.util.Crasher;
import com.fr.swift.util.Util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * 数据库操作
 */
public class DBSourceUtils {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(DBSourceUtils.class);

    public static SwiftMetaData getTableMetaData(final Connection connection, final String schema, final String table) {
        Util.requireNonNull(table);
        Callable task = new Callable<SwiftMetaData>() {
            @Override
            public SwiftMetaData call() throws Exception {
                java.sql.Connection conn = null;
                try {
                    conn = connection.createConnection();
                    Dialect dialect = DialectFactory.generateDialect(conn, connection.getDriver());
                    String translatedTableName = getTransferColumnComment(connection, dialect.getTableCommentName(conn, table, schema, null));
                    List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
                    List<FieldMessage> messages = dialect.getTableFieldsMessage(conn, table, schema, null);
                    for (FieldMessage message : messages) {
                        String comment = getTransferColumnComment(connection, message.getColumnComment());
                        columnList.add(new MetaDataColumnBean(message.getColumnName(), comment, message.getColumnType(), message.getColumnSize(), message.getColumnDecimalDigits()));
                    }
                    return new SwiftMetaDataBean(table, translatedTableName, schema, columnList);
                } finally {
                    DBUtils.closeConnection(conn);
                }
            }
        };
        RetryLoop retryLoop = new RetryLoop();
        retryLoop.initial(new RetryNTimes(PerformancePlugManager.getInstance().getRetryMaxTimes(), PerformancePlugManager.getInstance().getRetryMaxSleepTime()));
        try {
            return (SwiftMetaData) RetryLoop.retry(task, retryLoop);
        } catch (Exception e) {
            return Crasher.crash(e);
        }
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

    public static SwiftMetaData getServerMetaData(String serverTableName) {
        if (StringUtils.isNotBlank(serverTableName)) {
            DatasourceManagerProvider datasourceManager = DatasourceManager.getProviderInstance();
            TableData tableData = datasourceManager.getTableData(serverTableName);
            if (tableData == null) {
                return Crasher.crash("can not find server db :" + serverTableName);
            }
            if (tableData instanceof DBTableData) {
                return getServerMetaData((DBTableData) tableData, serverTableName);
            } else if (tableData != null) {
                return getMetaDataOnlyByTableData(tableData, serverTableName);
            }
        }
        return Crasher.crash(" server db name is blank");
    }

    private static SwiftMetaData getMetaDataOnlyByTableData(TableData tableData, String serverTableName) {
        DataModel dm = null;
        try {
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
            int rowCount = dm.getRowCount();
            List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
            for (int i = 0; i < cols; i++) {
                SwiftMetaDataColumn column = new MetaDataColumnBean(dm.getColumnName(i), rowCount == 0 ? java.sql.Types.VARCHAR : resloveValue(dm.getValueAt(0, i)));
                columnList.add(column);
            }
            return new SwiftMetaDataBean(serverTableName, columnList);
        } catch (Exception e) {
            return Crasher.crash(e);
        } finally {
            if (dm != null) {
                try {
                    dm.release();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 数据集这边简单判断下类型吻合
     *
     * @param value 值
     * @return 类型吻合
     */
    public static int resloveValue(Object value) {
        if (value instanceof String || value instanceof Boolean) {
            return java.sql.Types.VARCHAR;
        } else if (value instanceof Date || value instanceof java.sql.Date) {
            return java.sql.Types.DATE;
        } //所有NUMBER类型全部转换成DOUBLE
        else if (value instanceof Number) {
            return java.sql.Types.DOUBLE;
        } else {
            return java.sql.Types.BINARY;
        }
    }

    private static SwiftMetaData getServerMetaData(final DBTableData tableData, final String tableName) {
        return getQueryMetaData(tableData.getDatabase(), tableData.getQuery(), tableName);
    }

    public static SwiftMetaData getQueryMetaData(final Connection frConnection, final String query) {
        return getQueryMetaData(frConnection, query, query.substring(0, 10));
    }

    private static SwiftMetaData getQueryMetaData(final Connection frConnection, final String query, final String tableName) {
        Util.requireNonNull(query);
        Callable task = new Callable<SwiftMetaData>() {
            @Override
            public SwiftMetaData call() throws Exception {
                java.sql.Connection conn = null;
                try {
                    conn = frConnection.createConnection();
                    Dialect dialect = DialectFactory.generateDialect(conn, frConnection.getDriver());
                    ColumnInformation[] columns = DBUtils.checkInColumnInformation(conn, dialect, query);
                    List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
                    for (int i = 0, cols = columns.length; i < cols; i++) {
                        columnList.add(new MetaDataColumnBean(columns[i].getColumnName(), columns[i].getColumnType(), columns[i].getColumnSize(), columns[i].getScale()));
                    }
                    return new SwiftMetaDataBean(tableName, columnList);
                } finally {
                    DBUtils.closeConnection(conn);
                }
            }
        };
        RetryLoop retryLoop = new RetryLoop();
        retryLoop.initial(new RetryNTimes(PerformancePlugManager.getInstance().getRetryMaxTimes(), PerformancePlugManager.getInstance().getRetryMaxSleepTime()));
        try {
            return (SwiftMetaData) RetryLoop.retry(task, retryLoop);
        } catch (Exception e) {
            return Crasher.crash(e);
        }
    }

}
