package com.fr.bi.cal.report.db;

import com.fr.base.FRContext;
import com.fr.data.core.db.ColumnInformation;
import com.fr.data.core.db.DBUtils;
import com.fr.data.core.db.TableProcedure;
import com.fr.data.core.db.dialect.AbstractDialect;
import com.fr.data.core.db.dml.Table;
import com.fr.data.impl.Connection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 阿里云ads上的mysql, 用odbc的方式连接.
 * 从metadata里获取不到所有表的信息, 但是直接写sql是可以查出来的.
 * 装了插件后, 在odbc连接方式下依然会先从默认逻辑获取所有表, 如果获取不到, 那么就走dialect获取.
 *
 * Created by Administrator on 2015/12/17 0017.
 */
public class AdsMysqlOdbcDialect extends AbstractDialect {


    /**
     * 获取列信息的sql
     *
     * @param query 原始sql
     * @return 获取列信息的sql
     */
    @Override
	public String createSQL4Columns(String query) {
        return query;
    }

    /**
     * 获取所有表信息
     *
     * @return 数据库表信息
     */
    @Override
	public TableProcedure[] getAllTableProcedure(Connection database, String type) {
        java.sql.Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            List sqlTableList = new ArrayList();
            connection = database.createConnection();

            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES");
            while (resultSet.next()) {
                sqlTableList.add(new TableProcedure(null, resultSet.getString(1), type, this));
            }
            return (TableProcedure[]) sqlTableList.toArray(new TableProcedure[sqlTableList.size()]);
        } catch (Exception e) {
            FRContext.getLogger().error(e.getMessage());
        } finally {
            release(connection, statement, resultSet);
        }

        return new TableProcedure[0];
    }

    private void release(java.sql.Connection connection, Statement statement, ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }

            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {

        }
    }

    @Override
	public List getTableFieldsInfor(java.sql.Connection conn, String tableName, String schema, String dbLink){
        String query = "select * from " + (schema == null ? "" : (schema + ".")) + tableName;
        List result = new ArrayList();

        try {
            ColumnInformation[] informations = DBUtils.checkInColumnInformation(conn, this, query);
            for(ColumnInformation information : informations){
                Map field = new HashMap();
                field.put("column_name", information.getColumnName());
                field.put("column_comment", "");
                field.put("column_type", information.getColumnType());
                field.put("column_size", information.getColumnSize());
                field.put("column_key", false);
                result.add(field);
            }

        } catch (SQLException e) {
            FRContext.getLogger().error(e.getMessage());
        }

        return result;
    }

    @Override
    public String getTopNRowSql(int row, Table table) {
        return "select * from " + this.table2SQL(table)+ " limit 0, " + row;
    }



}