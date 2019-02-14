package com.fr.swift.exception.meta;

/**
 * @author pony
 * @date 2017/11/20
 */
public class SwiftMetaDataAbsentException extends SwiftMetaDataException {

    public SwiftMetaDataAbsentException(String tableName) {
        super(String.format("table : %s absent !", tableName));
    }
}
