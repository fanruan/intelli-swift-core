package com.fr.swift.jdbc.result;

import com.fr.swift.jdbc.exception.Exceptions;
import com.fr.swift.source.Row;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author yee
 * @date 2018-12-19
 */
public class MaintainResultSet extends BaseResultSet {

    private List<String> columnNames;
    private Iterator<Row> iterator;

    public MaintainResultSet(Iterator<Row> iterator, List<String> columnNames) {
        this.iterator = iterator;
        this.columnNames = columnNames;
    }

    @Override
    public boolean next() throws SQLException {
        if (iterator.hasNext()) {
            current = iterator.next();
            return true;
        }
        return false;
    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return new BaseResultSetMetaData() {
            @Override
            public int getColumnCount() throws SQLException {
                return columnNames.size();
            }

            @Override
            public int getColumnDisplaySize(int column) throws SQLException {
                return 0;
            }

            @Override
            public String getColumnLabel(int column) throws SQLException {
                return columnNames.get(column - 1);
            }

            @Override
            public String getColumnName(int column) throws SQLException {
                return columnNames.get(column - 1);
            }

            @Override
            public String getSchemaName(int column) throws SQLException {
                return null;
            }

            @Override
            public int getPrecision(int column) throws SQLException {
                return 0;
            }

            @Override
            public int getScale(int column) throws SQLException {
                return 0;
            }

            @Override
            public String getTableName(int column) throws SQLException {
                return null;
            }

            @Override
            public String getCatalogName(int column) throws SQLException {
                return null;
            }

            @Override
            public int getColumnType(int column) throws SQLException {
                Object data = current.getValue(column - 1);
                if (null != data) {
                    if (data instanceof Double) {
                        return Types.DOUBLE;
                    }
                    if (data instanceof Integer) {
                        return Types.INTEGER;
                    }
                    if (data instanceof Date) {
                        return Types.DATE;
                    }
                    if (data instanceof Number) {
                        return Types.BIGINT;
                    }
                }
                return Types.VARCHAR;
            }
        };
    }

    @Override
    public int findColumn(String columnLabel) throws SQLException {
        if (columnNames.contains(columnLabel)) {
            return columnNames.indexOf(columnLabel) + 1;
        }
        throw Exceptions.sql(columnLabel + " not found");
    }
}
