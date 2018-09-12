package com.fr.swift.exception.meta;

/**
 * @author pony
 * @date 2017/11/20
 */
public class SwiftMetaDataColumnAbsentException extends SwiftMetaDataException {
    public SwiftMetaDataColumnAbsentException(String tableName, int columnIndex) {
        super(String.format("table %s columnIndex %d absent!", tableName, columnIndex));
    }

    public SwiftMetaDataColumnAbsentException(String tableName, String columnName) {
        super(String.format("table %s column %s absent!", tableName, columnName));
    }
}