package com.fr.swift.exception.meta;

/**
 * Created by pony on 2017/11/20.
 */
public class SwiftMetaDataColumnAbsentException extends SwiftMetaDataException {
    private int columnIndex;

    private String columnName;

    public SwiftMetaDataColumnAbsentException(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public SwiftMetaDataColumnAbsentException(String columnName) {
        this.columnName = columnName;
    }
}
