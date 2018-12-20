package com.fr.swift.jdbc.result;

import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;

/**
 *
 * @author pony
 * @date 2018/8/17
 */
public class MetadataWrapper extends BaseResultSetMetaData {
    private SwiftMetaData metaData;

    public MetadataWrapper(SwiftMetaData metaData) {
        this.metaData = metaData;
    }

    @Override
    public int getColumnCount() throws SQLException {
        return metaData.getColumnCount();
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
}
