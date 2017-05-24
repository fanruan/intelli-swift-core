package com.fr.bi.stable.data.db;/**
 * This class created on 2017/5/24.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.data.impl.Connection;

/**
 * @deprecated 封装SQL语句，兼容SQLStatement
 *
 *
 *
 */
public class FakeSQLStatement extends SQLStatement {
    private final static BILogger LOGGER = BILoggerFactory.getLogger(FakeSQLStatement.class);
    private String sqlStatement;

    public FakeSQLStatement(Connection conn, String sqlStatement) {
        super(conn);
        this.sqlStatement = sqlStatement;
    }


    @Override
    public String getSelect() {
        return sqlStatement;
    }

    @Override
    public void setSelect(String select) {

    }

    @Override
    public String getFrom() {
        return sqlStatement;
    }

    @Override
    public void setFrom(String from) {

    }

    @Override
    public String getWhere() {
        return sqlStatement;
    }

    @Override
    public void setWhere(String where) {

    }

    @Override
    public String getSchema() {
        return super.getSchema();
    }

    @Override
    public void setSchema(String schema) {
        super.setSchema(schema);
    }

    @Override
    public String getTableName() {
        return sqlStatement;

    }

    @Override
    public void setTableName(String tableName) {
    }

    @Override
    public String toString() {
        return sqlStatement;
    }
}
