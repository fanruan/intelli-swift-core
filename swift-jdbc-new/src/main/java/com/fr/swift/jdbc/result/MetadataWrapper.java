package com.fr.swift.jdbc.result;

import com.fr.swift.jdbc.exception.Exceptions;
import com.fr.swift.source.SwiftMetaData;

import javax.naming.OperationNotSupportedException;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

/**
 *
 * @author pony
 * @date 2018/8/17
 */
public class MetadataWrapper implements ResultSetMetaData {
    private SwiftMetaData metaData;

    public MetadataWrapper(SwiftMetaData metaData) {
        this.metaData = metaData;
    }

    @Override
    public int getColumnCount() throws SQLException {
        return metaData.getColumnCount();
    }

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
    public int getColumnDisplaySize(int column) throws SQLException {
        return metaData.getColumn(column).getPrecision();
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        return metaData.getColumnName(column);
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        return metaData.getColumnName(column);
    }

    @Override
    public String getSchemaName(int column) throws SQLException {
        return metaData.getSchemaName();
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        return metaData.getColumn(column).getPrecision();
    }

    @Override
    public int getScale(int column) throws SQLException {
        return metaData.getColumn(column).getScale();
    }

    @Override
    public String getTableName(int column) throws SQLException {
        return metaData.getTableName();
    }

    @Override
    public String getCatalogName(int column) {
        return metaData.getSwiftDatabase().getName();
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        return metaData.getColumnType(column);
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
    public String getColumnClassName(int column) {
        throw Exceptions.runtime(new OperationNotSupportedException());
    }

    @Override
    public <T> T unwrap(Class<T> iface) {
        throw Exceptions.runtime(new OperationNotSupportedException());
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        return false;
    }
}
