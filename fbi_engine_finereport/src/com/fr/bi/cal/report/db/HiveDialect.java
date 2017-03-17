package com.fr.bi.cal.report.db;

import com.fr.data.core.db.dialect.AbstractDialect;
import com.fr.data.core.db.dml.Table;

/**
 * Created by roy on 2016/12/21.
 */
public class HiveDialect extends AbstractDialect {
    @Override
    public String getTopNRowSql(int row, Table table) {
        return "select * from " + this.table2SQL(table) + " limit 0, " + row;
    }
}
