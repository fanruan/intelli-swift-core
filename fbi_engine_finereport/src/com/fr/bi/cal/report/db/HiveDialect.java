package com.fr.bi.cal.report.db;

import com.fr.data.core.db.dml.Table;

import java.sql.Connection;

/**
 * Created by roy on 2016/12/21.
 */
public class HiveDialect extends com.fr.data.core.db.dialect.HiveDialect {
    @Override
    public String getTopNRowSql(int row, Table table) {
        return "select * from " + this.table2SQL(table) + " limit 0, " + row;
    }

    @Override
    public String defaultValidationQuery(Connection connection) {
        return super.defaultValidationQuery(connection);
    }
}
