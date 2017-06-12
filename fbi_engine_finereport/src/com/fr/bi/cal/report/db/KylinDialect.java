package com.fr.bi.cal.report.db;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.data.core.db.dialect.AbstractDialect;
import com.fr.data.core.db.dml.Table;
import com.fr.stable.StringUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * wang 2017/6/9
 */
public class KylinDialect extends AbstractDialect {
    @Override
    public String getTopNRowSql(int row, Table table) {
        return "select * from " + this.table2SQL(table) + " limit " + row;
    }
    @Override
    public String defaultValidationQuery(Connection conn) {
        try {
            DatabaseMetaData e = conn.getMetaData();
            ResultSet rs1 = e.getTableTypes();
            while(rs1.next()) {
                if("TABLE".equals(rs1.getString("TABLE_TYPE"))) {
                    ResultSet res = e.getTables((String)null, (String)null, "%", new String[]{"TABLE"});
                    String tableName;
                    do {
                        if(!res.next()) {
                            return "";
                        }
                        tableName = res.getString("TABLE_NAME");
                    } while(!StringUtils.isNotBlank(tableName));
                    String validationQuery = "select count(*) from " + tableName ;
                    BILoggerFactory.getLogger(KylinDialect.class).info("try ValidationQuery:" + validationQuery);
                    return validationQuery;
                }
            }
        } catch (SQLException var21) {
            try {
                if(conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException var20) {
            }
            String rs = "";
            return rs;
        } finally {
            try {
                if(conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException var19) {
            }

        }
        return "";
    }

    @Override
    public String table2SQL(Table table) {
        if(table == null) {
            return "";
        } else if(StringUtils.isNotBlank(this.left_quote) && StringUtils.isNotBlank(this.right_quote)) {
            StringBuffer sb = new StringBuffer();
            sb.append(this.column2SQL(table.getName()));
            return sb.toString();
        } else {
            return table.getName();
        }
    }
}
