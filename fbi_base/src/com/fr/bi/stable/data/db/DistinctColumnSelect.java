package com.fr.bi.stable.data.db;

import com.fr.data.core.db.dialect.Dialect;
import com.fr.data.core.db.dialect.DialectFactory;
import com.fr.data.core.db.dml.AbstractDML;
import com.fr.data.core.db.dml.Table;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by User on 2015/12/2.
 */
public class DistinctColumnSelect extends AbstractDML {
    private String distinctColumnName;

    /**
     * 根据指定的表和方言生成数据库的select操作
     *
     * @param table   数据库表
     * @param dialect 方言
     */
    public DistinctColumnSelect(Table table, String columnName, Dialect dialect) {
        super(table, dialect);

        this.distinctColumnName = columnName;
    }

    @Override
    protected PreparedStatement createPreparedStatementByValidatedParameters(
            java.sql.Connection conn) throws SQLException {
        String selectSQL = this.toSQL();
        return conn.prepareStatement(selectSQL);
    }

    public String toSQL() {
        if (dialect == null) {
            dialect = DialectFactory.getDefaultDialect();
        }

        return "SELECT distinct " + dialect.column2SQL(this.distinctColumnName) + " FROM " + getTable().toStatementSQLString(dialect);
    }

}