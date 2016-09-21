package com.fr.bi.util;

import com.fr.bi.stable.data.db.SQLStatement;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.data.core.db.ColumnInformation;
import com.fr.data.core.db.DBUtils;
import com.fr.data.core.db.dialect.Dialect;
import com.fr.data.core.db.dialect.DialectFactory;
import com.fr.stable.StringUtils;

/**
 * This class created on 2016/6/7.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeDBUtils {

    public static String getColumnName(com.fr.data.impl.Connection connection, SQLStatement statement, String sql) {
        try {
            java.sql.Connection conn = statement.getSqlConn();
            Dialect dialect = DialectFactory.generateDialect(conn, connection.getDriver());
            ColumnInformation column = DBUtils.checkInColumnInformation(conn, dialect, sql)[0];
            return column.getColumnName();
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return StringUtils.EMPTY;
    }

    public static int getColumnNum(com.fr.data.impl.Connection connection, SQLStatement statement, String sql) {
        try {
            java.sql.Connection conn = statement.getSqlConn();
            Dialect dialect = DialectFactory.generateDialect(conn, connection.getDriver());
            ColumnInformation column = DBUtils.checkInColumnInformation(conn, dialect, sql)[0];
            return DBUtils.checkInColumnInformation(conn, dialect, sql).length;
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return 0;
    }
}
