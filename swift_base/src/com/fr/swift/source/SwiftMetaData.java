package com.fr.swift.source;

import com.fr.swift.exception.meta.SwiftMetaDataException;

/**
 * Created by pony on 2017/10/24.
 * 先实现一些基本的功能，以后如果需要兼容jdbc，可扩展为ResultSetMetadata
 * column 从1开始
 */
public interface SwiftMetaData extends Iterable<SwiftMetaDataColumn> {
    String getTableName() throws SwiftMetaDataException;

    /**
     * @param column the first column is 1, the second is 2
     * @return
     * @throws SwiftMetaDataException
     */
    String getTableName(int column) throws SwiftMetaDataException;

    int getColumnCount() throws SwiftMetaDataException;

    /**
     * @param column the first column is 1, the second is 2
     * @return
     * @throws SwiftMetaDataException
     */
    String getColumnName(int column) throws SwiftMetaDataException;

    /**
     * @param column the first column is 1, the second is 2
     * @return
     * @throws SwiftMetaDataException
     */
    String getColumnRemark(int column) throws SwiftMetaDataException;

    String getSchemaName() throws SwiftMetaDataException;

    /**
     * @param column the first column is 1, the second is 2
     * @return
     * @throws SwiftMetaDataException
     */
    String getSchemaName(int column) throws SwiftMetaDataException;

    /**
     * @param column the first column is 1, the second is 2
     * @return
     * @throws SwiftMetaDataException
     */
    int getScale(int column) throws SwiftMetaDataException;

    /**
     * @param column the first column is 1, the second is 2
     * @return
     * @throws SwiftMetaDataException
     */
    int getColumnType(int column) throws SwiftMetaDataException;

    /**
     * @param column the first column is 1, the second is 2
     * @return
     * @throws SwiftMetaDataException
     */
    int getPrecision(int column) throws SwiftMetaDataException;

    SwiftMetaDataColumn getColumn(int column) throws SwiftMetaDataException;

    SwiftMetaDataColumn getColumn(String columnName) throws SwiftMetaDataException;

    int getColumnIndex(String columnName) throws SwiftMetaDataException;
}
