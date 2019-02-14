package com.fr.swift.source.db;

import com.fr.data.core.db.DBUtils;
import com.fr.data.core.db.dialect.Dialect;
import com.fr.data.core.db.dialect.DialectFactory;
import com.fr.data.core.db.dialect.base.DialectKeyConstants;
import com.fr.data.core.db.dialect.base.key.create.statement.limit.DialectCreateLimitedFetchedStatementParameter;
import com.fr.general.DateUtils;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.setting.PerformancePlugManager;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.SwiftSourceTransfer;
import com.fr.swift.source.db.dbdealer.DBDealer;
import com.fr.swift.source.db.dbdealer.Date2StringConvertDealer;
import com.fr.swift.source.db.dbdealer.DateDealer;
import com.fr.swift.source.db.dbdealer.DoubleDealer;
import com.fr.swift.source.db.dbdealer.LongDealer;
import com.fr.swift.source.db.dbdealer.Number2DateConvertDealer;
import com.fr.swift.source.db.dbdealer.Number2StringConvertDealer;
import com.fr.swift.source.db.dbdealer.String2DateConvertDealer;
import com.fr.swift.source.db.dbdealer.String2NumberConvertDealer;
import com.fr.swift.source.db.dbdealer.StringDealer;
import com.fr.swift.source.db.dbdealer.StringDealerWithCharSet;
import com.fr.swift.source.db.dbdealer.TimeDealer;
import com.fr.swift.source.db.dbdealer.TimestampDealer;
import com.fr.swift.source.retry.RetryLoop;
import com.fr.swift.source.retry.RetryNTimes;
import com.fr.swift.util.Strings;
import com.fr.swift.util.Util;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by pony on 2017/12/5.
 */
public abstract class AbstractQueryTransfer implements SwiftSourceTransfer {
    protected static final SwiftLogger LOGGER = SwiftLoggers.getLogger(AbstractQueryTransfer.class);
    protected ConnectionInfo connectionInfo;

    public AbstractQueryTransfer(ConnectionInfo connectionInfo) {
        Util.requireNonNull(connectionInfo);
        this.connectionInfo = connectionInfo;
    }

    @Override
    public SwiftResultSet createResultSet() {
        Callable<SwiftResultSet> task = new Callable<SwiftResultSet>() {
            @Override
            public SwiftResultSet call() throws Exception {
                SwiftResultSet iterator = null;
                String sql = null;
                com.fr.data.impl.Connection connection = connectionInfo.getFrConnection();
                Connection conn = null;
                Statement stmt = null;
                ResultSet rs = null;
                try {
                    long t = System.currentTimeMillis();
                    conn = connection.createConnection();
                    String originalCharSetName = connection.getOriginalCharsetName();
                    String newCharSetName = connection.getNewCharsetName();
                    boolean needCharSetConvert = Strings.isNotBlank(originalCharSetName)
                            && Strings.isNotBlank(newCharSetName);
                    Dialect dialect = DialectFactory.generateDialect(conn, connection.getDriver());
                    sql = getQuery(dialect);
                    LOGGER.info("runSQL " + sql);

                    if (Strings.isNotEmpty(sql)) {
                        sql = dealWithSqlCharSet(sql, connection);
                        stmt = createStatement(conn, dialect);
                        rs = stmt.executeQuery(sql);
                        LOGGER.info("sql: " + sql + " query cost:" + DateUtils.timeCostFrom(t));
                        iterator = createIterator(rs, dialect, sql, stmt, conn, needCharSetConvert, originalCharSetName, newCharSetName);
                        LOGGER.info("sql: " + sql + " execute cost:" + DateUtils.timeCostFrom(t));
                    } else {
                        iterator = EMPTY;
                    }
                } catch (Exception e) {
                    LOGGER.error("sql: " + sql + " execute failed!");
                    close(rs, stmt, conn);
                    throw e;
                }
                return iterator;
            }
        };

        RetryLoop retryLoop = new RetryLoop();
        retryLoop.initial(new RetryNTimes(PerformancePlugManager.getInstance().getRetryMaxTimes(), PerformancePlugManager.getInstance().getRetryMaxSleepTime()));
        try {
            return RetryLoop.retry(task, retryLoop);
        } catch (Exception e) {
            LOGGER.error(e);
            return EMPTY;
        }
    }

    protected void close(ResultSet rs, Statement stmt, Connection conn) {
        DBUtils.closeResultSet(rs);
        DBUtils.closeStatement(stmt);
        DBUtils.closeConnection(conn);
    }

    protected abstract String getQuery(Dialect dialect) throws SQLException;

    private String dealWithSqlCharSet(String sql, com.fr.data.impl.Connection database) {
        if (Strings.isNotBlank(database.getOriginalCharsetName()) && Strings.isNotBlank(database.getNewCharsetName())) {
            try {
                return new String(sql.getBytes(database.getNewCharsetName()), database.getOriginalCharsetName());
            } catch (UnsupportedEncodingException e) {
                SwiftLoggers.getLogger().error(e);
            }
        }
        return sql;
    }

    public Statement createStatement(Connection conn, Dialect dialect) throws SQLException {
        return dialect.execute(DialectKeyConstants.CREATE_LIMITED_FETCHED_STATEMENT_KEY, new DialectCreateLimitedFetchedStatementParameter(conn));
    }

    protected abstract SwiftResultSet createIterator(ResultSet rs,
                                                     Dialect dialect, String sql, Statement stmt, Connection conn, boolean needCharSetConvert,
                                                     String originalCharSetName,
                                                     String newCharSetName) throws SQLException;

    /**
     * @param needCharSetConvert
     * @param originalCharSetName
     * @param newCharSetName
     * @param metaData           swift保存的metadata
     * @param sqlMeta            发往数据库的metadata
     * @return
     * @throws SQLException
     */
    protected DBDealer[] createDBDealer(boolean needCharSetConvert, String originalCharSetName,
                                        String newCharSetName, SwiftMetaData metaData, SwiftMetaData sqlMeta) throws SQLException {
        List<DBDealer> res = new ArrayList<DBDealer>();
        for (int i = 0; i < sqlMeta.getColumnCount(); i++) {
            int rsColumn = i + 1;
            String name = sqlMeta.getColumnName(rsColumn);
            SwiftMetaDataColumn column = null;
            try {
                column = metaData.getColumn(name);
            } catch (Exception e) {
                //do nothing
            }
            if (column != null) {
                DBDealer dealer = getDbDealer(needCharSetConvert, originalCharSetName, newCharSetName, column, sqlMeta, rsColumn);
                res.add(dealer);
            }
        }
        return res.toArray(new DBDealer[res.size()]);
    }

    private DBDealer getDbDealer(boolean needCharSetConvert, String originalCharSetName, String newCharSetName, SwiftMetaDataColumn column, SwiftMetaData sqlMeta, int rsColumn) throws SwiftMetaDataException {
        int outerSqlType = sqlMeta.getColumnType(rsColumn);
        ColumnType columnType = ColumnTypeUtils.sqlTypeToColumnType(column.getType(), column.getPrecision(), column.getScale());
        switch (outerSqlType) {
            case java.sql.Types.DECIMAL:
            case java.sql.Types.NUMERIC:
            case java.sql.Types.REAL:
            case java.sql.Types.DOUBLE:
            case java.sql.Types.FLOAT:
                return getDealerByColumn(sqlMeta.getPrecision(rsColumn), sqlMeta.getScale(rsColumn), rsColumn, columnType);
            case java.sql.Types.BIT:
            case java.sql.Types.TINYINT:
            case java.sql.Types.SMALLINT:
            case java.sql.Types.INTEGER:
            case java.sql.Types.BIGINT:
                return getNumberConvertDealer(new LongDealer(rsColumn), columnType);
            case java.sql.Types.DATE:
                return getDateConvertDealer(new DateDealer(rsColumn), columnType);
            case java.sql.Types.TIMESTAMP:
                return getDateConvertDealer(new TimestampDealer(rsColumn), columnType);
            case java.sql.Types.TIME:
                return getDateConvertDealer(new TimeDealer(rsColumn), columnType);
            default:
                if (needCharSetConvert) {
                    return getStringConvertDealer(new StringDealerWithCharSet(rsColumn, originalCharSetName, newCharSetName), columnType);
                } else {
                    return getStringConvertDealer(new StringDealer(rsColumn), columnType);
                }
        }
    }

    private DBDealer getDealerByColumn(int precision, int scale, int rsColumn, ColumnType columnType) {
        if (scale == 0 && ColumnTypeUtils.isLongType(precision)) {
            //没有小数点，并且判断为long类型（长度小于19，或者强制设置成数值类型），都去取long再转化，否则一律getdouble
            return getNumberConvertDealer(new LongDealer(rsColumn), columnType);
        } else {
            return getNumberConvertDealer(new DoubleDealer(rsColumn), columnType);
        }
    }


    private DBDealer getDateConvertDealer(DBDealer<Long> dateDealer, ColumnType columnType) {
        switch (columnType) {
            case STRING:
                return new Date2StringConvertDealer(dateDealer);
            default:
                return dateDealer;

        }
    }

    private DBDealer getStringConvertDealer(DBDealer<String> stringDealer, ColumnType columnType) {
        switch (columnType) {
            case NUMBER:
                return new String2NumberConvertDealer(stringDealer);
            case DATE:
                return new String2DateConvertDealer(stringDealer);
            default:
                return stringDealer;

        }
    }

    private DBDealer getNumberConvertDealer(DBDealer<? extends Number> numberDBDealer, ColumnType columnType) {
        switch (columnType) {
            case STRING:
                return new Number2StringConvertDealer(numberDBDealer);
            case DATE:
                return new Number2DateConvertDealer(numberDBDealer);
            default:
                return numberDBDealer;

        }
    }
}
