package com.fr.swift.exception.meta;

/**
 * Created by pony on 2017/11/20.
 */
public class SwiftMetaDataColumnAbsentException extends SwiftMetaDataException {

    public SwiftMetaDataColumnAbsentException(String tableName, int columnIndex) {
        super("table : " + tableName + " columnIndex " + columnIndex + " absent !" );
    }

    public SwiftMetaDataColumnAbsentException(String tableName, String columnName) {
        super("table : " + tableName + " column " + columnName + " absent !" );
    }

}
