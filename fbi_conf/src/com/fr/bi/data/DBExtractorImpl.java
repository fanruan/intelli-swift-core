package com.fr.bi.data;

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.base.FRContext;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.constant.CubeConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.db.SQLStatement;
import com.fr.bi.stable.dbdealer.*;
import com.fr.bi.stable.utils.BIDBUtils;
import com.fr.bi.stable.utils.time.BIDateUtils;
import com.fr.data.core.db.DBUtils;
import com.fr.data.core.db.dialect.Dialect;
import com.fr.data.core.db.dialect.DialectFactory;
import com.fr.general.DateUtils;
import com.fr.stable.StringUtils;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2016/8/11.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public abstract class DBExtractorImpl implements DBExtractor {
    private static final BILogger logger = BILoggerFactory.getLogger(DBExtractorImpl.class);

    private int dealWithResultSet(ResultSet rs,
                                  ICubeFieldSource[] columns,
                                  Traversal<BIDataValue> traversal,
                                  boolean needCharSetConvert,
                                  String originalCharSetName,
                                  String newCharSetName, int row, String sql) throws SQLException {
        @SuppressWarnings("rawtypes")
        DBDealer[] dealers = createDBDealer(needCharSetConvert, originalCharSetName, newCharSetName, columns);
        int ilen = dealers.length;
        while (rs.next()) {
            for (int i = 0; i < ilen; i++) {
                Object value = dealers[i].dealWithResultSet(rs);
                traversal.actionPerformed(new BIDataValue(row, i, value));
            }
            row++;
            if (CubeConstant.LOG_SEPERATOR_ROW != 0 && row % CubeConstant.LOG_SEPERATOR_ROW == 0) {
                logger.info(BIDateUtils.getCurrentDateTime() + " sql: " + trimSQL(sql) + "is executing…… " + " transported rows：" + row);
            }
        }
        return row;
    }

    private static String trimSQL(String sql) {
        StringBuffer sb = new StringBuffer();
        if (sql.length() > 110) {
            sb.append(sql.substring(0, 50));
            sb.append("...");
            sb.append(sql.substring(sql.length() - 50, sql.length()));
            return sb.toString();
        } else {
            return sql;
        }
    }


    @SuppressWarnings("rawtypes")
    private DBDealer[] createDBDealer(boolean needCharSetConvert, String originalCharSetName,
                                      String newCharSetName, ICubeFieldSource[] columns) {
        List<DBDealer> res = new ArrayList<DBDealer>();
        for (int i = 0, ilen = columns.length; i < ilen; i++) {
            ICubeFieldSource field = columns[i];
            if (field.isUsable()) {
                DBDealer object = null;
                int rsColumn = i + 1;
                switch (field.getFieldType()) {
                    case DBConstant.COLUMN.DATE: {
//                        switch (field.getClassType()) {
//                           case DBConstant.CLASS.DATE : {
//                               object = new DateDealer(rsColumn);
//                               break;
//                           }
//                            case DBConstant.CLASS.TIME : {
//                                object = new TimeDealer(rsColumn);
//                                break;
//                            }
//                            case DBConstant.CLASS.TIMESTAMP : {
//                                object = new TimestampDealer(rsColumn);
//                                break;
//                            }
//                            default: {
//                                object = new TimestampDealer(rsColumn);
//                            }
//                        }
                        object = new TimestampDealer(rsColumn);
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

    /**
     * 执行sql语句，获取数据
     *
     * @param traversal
     */
    @Override
    public int runSQL(SQLStatement sql, ICubeFieldSource[] columns, Traversal<BIDataValue> traversal, int row) {
        com.fr.data.impl.Connection connection = sql.getConn();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            long t = System.currentTimeMillis();
            conn = sql.getSqlConn();
            String originalCharSetName = connection.getOriginalCharsetName();
            String newCharSetName = connection.getNewCharsetName();
            boolean needCharSetConvert = StringUtils.isNotBlank(originalCharSetName)
                    && StringUtils.isNotBlank(newCharSetName);
            Dialect dialect = DialectFactory.generateDialect(conn, connection.getDriver());
            logger.info("runSQL tableFrom is:" + sql.getFrom());
            if (columns.length == 0) {
                logger.info("runSQL error : columns is empty");
            } else {
                for (ICubeFieldSource column : columns) {
                    logger.info("runSQL field is:" + column.getFieldName() + " fieldType is:" + column.getFieldType() + " table is:" + column.getTableBelongTo().getTableName());
                }
            }
            String sqlString = BIDBUtils.createSqlString(dialect, columns);
            sql.setSelect(sqlString);
            String query = dealWithSqlCharSet(sql.toString(), connection);
            BILoggerFactory.getLogger().info("Start Query sql:" + query);
            stmt = createStatement(conn, dialect);
            try {
                rs = stmt.executeQuery(query);
            } catch (Exception e) {
                DBUtils.closeStatement(stmt);
                sql.setSelect("");
                query = dealWithSqlCharSet(sql.toString(), connection);
                stmt = createStatement(conn, dialect);
                rs = stmt.executeQuery(query);
            }
            logger.info("sql: " + sql.toString() + " query cost:" + DateUtils.timeCostFrom(t));
            row = dealWithResultSet(rs, columns, traversal, needCharSetConvert, originalCharSetName, newCharSetName, row, sql.toString());
            logger.info("sql: " + sql.toString() + " execute cost:" + DateUtils.timeCostFrom(t));
        } catch (Throwable e) {
            logger.error("sql: " + sql.toString() + " execute failed!");
            throw new RuntimeException(e);
        } finally {
            DBUtils.closeResultSet(rs);
            DBUtils.closeStatement(stmt);
            DBUtils.closeConnection(conn);
        }
        return row;
    }


    private String dealWithSqlCharSet(String sql, com.fr.data.impl.Connection database) {
        if (StringUtils.isNotBlank(database.getOriginalCharsetName()) && StringUtils.isNotBlank(database.getNewCharsetName())) {
            try {
                return new String(sql.getBytes(database.getNewCharsetName()), database.getOriginalCharsetName());
            } catch (UnsupportedEncodingException e) {
                FRContext.getLogger().error(e.getMessage(), e);
            }
        }
        return sql;
    }

    public abstract Statement createStatement(Connection conn, Dialect dialect) throws SQLException;


}
