package com.fr.bi.util;

import com.fr.base.FRContext;
import com.fr.bi.conf.log.BILogManager;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.provider.BILogManagerProvider;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.db.SQLStatement;
import com.fr.bi.stable.dbdealer.*;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.data.core.db.DBUtils;
import com.fr.data.core.db.dialect.Dialect;
import com.fr.data.core.db.dialect.DialectFactory;
import com.fr.general.DateUtils;
import com.fr.stable.StringUtils;
import com.fr.stable.bridge.StableFactory;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2016/6/7.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeDBUtils {
    private static int dealWithResultSet(ResultSet rs,
                                         ICubeFieldSource[] columns,
                                         Traversal<BIDataValue> traversal,
                                         boolean needCharSetConvert,
                                         String originalCharSetName,
                                         String newCharSetName, int row) throws SQLException {
        @SuppressWarnings("rawtypes")
        DBDealer[] dealers = createDBDealer(needCharSetConvert, originalCharSetName, newCharSetName, columns);
        int ilen = dealers.length;
//        BILogManager biLogManager = StableFactory.getMarkedObject(BILogManagerProvider.XML_TAG, BILogManager.class);
//        long columnTime=System.currentTimeMillis();
        while (rs.next()) {
            for (int i = 0; i < ilen; i++) {
                Object value = dealers[i].dealWithResultSet(rs);
                traversal.actionPerformed(new BIDataValue(row, i, value));
            }
            row++;
            /*每运行一千行为column存取一次log
            * 所有column时间一致
            * edit by wuk 此处取消，column现作为索引的容器*/
//            if (row%1000==0&&null != columns[0].getTableBelongTo().getPersistentTable()){
//                for (int i = 0; i < ilen; i++) {
//                    biLogManager.infoColumn(columns[0].getTableBelongTo().getPersistentTable(), columns[i].getFieldName(), System.currentTimeMillis() - columnTime, -999);
//                }
//            }
        }
//        for (int i = 0; i < ilen; i++) {
//            biLogManager.infoColumn(columns[0].getTableBelongTo().getPersistentTable(), columns[i].getFieldName(), System.currentTimeMillis() - columnTime, -999);
//        }
        return row;
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
    public static long runSQL(SQLStatement sql, ICubeFieldSource[] columns, Traversal<BIDataValue> traversal) {
        return runSQL(sql, columns, traversal, 0);
    }

    /**
     * 执行sql语句，获取数据
     *
     * @param traversal
     */
    public static int runSQL(SQLStatement sql, ICubeFieldSource[] columns, Traversal<BIDataValue> traversal, int row) {
        com.fr.data.impl.Connection connection = sql.getConn();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            BILogManager biLogManager = StableFactory.getMarkedObject(BILogManagerProvider.XML_TAG, BILogManager.class);
            long t = System.currentTimeMillis();
            conn = sql.getSqlConn();
            String originalCharSetName = connection.getOriginalCharsetName();
            String newCharSetName = connection.getNewCharsetName();
            boolean needCharSetConvert = StringUtils.isNotBlank(originalCharSetName)
                    && StringUtils.isNotBlank(newCharSetName);
            Dialect dialect = DialectFactory.generateDialect(conn, connection.getDriver());
            String sqlString = createSqlString(dialect, columns);
            sql.setSelect(sqlString);
            String query = dealWithSqlCharSet(sql.toString(), connection);
            BILogger.getLogger().info("Start Query sql:" + query);
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
            BILogger.getLogger().info("sql: " + sql.toString() + " execute cost:" + DateUtils.timeCostFrom(t));
            row = dealWithResultSet(rs, columns, traversal, needCharSetConvert, originalCharSetName, newCharSetName, row);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            DBUtils.closeResultSet(rs);
            DBUtils.closeStatement(stmt);
            DBUtils.closeConnection(conn);
        }
        return row;
    }
    private static String createSqlString(Dialect dialect, ICubeFieldSource[] columns) {
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
    private static String dealWithSqlCharSet(String sql, com.fr.data.impl.Connection database) {
        if (StringUtils.isNotBlank(database.getOriginalCharsetName()) && StringUtils.isNotBlank(database.getNewCharsetName())) {
            try {
                return new String(sql.getBytes(database.getNewCharsetName()), database.getOriginalCharsetName());
            } catch (UnsupportedEncodingException e) {
                FRContext.getLogger().error(e.getMessage(), e);
            }
        }
        return sql;
    }

    private static Statement createStatement(Connection conn, Dialect dialect) throws SQLException {
        Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        try {
            stmt.setFetchSize(dialect.getFetchSize());
        } catch (Exception e) {
        }
        return stmt;
    }


}
