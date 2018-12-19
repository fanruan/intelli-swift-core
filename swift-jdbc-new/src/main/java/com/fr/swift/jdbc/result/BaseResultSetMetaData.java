package com.fr.swift.jdbc.result;

import com.fr.swift.jdbc.exception.Exceptions;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author yee
 * @date 2018-12-19
 */
public abstract class BaseResultSetMetaData implements ResultSetMetaData {


    @Override
    public boolean isAutoIncrement(int column) {
        return false;
    }

    @Override
    public boolean isCaseSensitive(int column) {
        return true;
    }

    @Override
    public boolean isSearchable(int column) {
        return false;
    }

    @Override
    public boolean isCurrency(int column) {
        return false;
    }

    @Override
    public int isNullable(int column) {
        return ResultSetMetaData.columnNullable;
    }

    @Override
    public boolean isSigned(int column) {
        return false;
    }


    @Override
    public String getColumnTypeName(int column) throws SQLException {
        int jdbcType = getColumnType(column);
        switch (jdbcType) {
            case Types.INTEGER:
                return "INTEGER";
            case Types.DOUBLE:
                return "DOUBLE";
            case Types.DATE:
                return "DATE";
            case Types.BIGINT:
                return "BIGINT";
            default:
                return "VARCHAR";
        }
    }

    @Override
    public boolean isReadOnly(int column) {
        return false;
    }

    @Override
    public boolean isWritable(int column) {
        return false;
    }

    @Override
    public boolean isDefinitelyWritable(int column) {
        return false;
    }

    @Override
    public String getColumnClassName(int column) throws SQLException {
        throw Exceptions.notSupported("getColumnClassName is not supported");
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw Exceptions.notSupported("unwrap is not supported");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        return false;
    }
}
