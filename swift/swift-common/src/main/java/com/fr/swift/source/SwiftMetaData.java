package com.fr.swift.source;

import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.exception.meta.SwiftMetaDataException;

import java.util.List;

/**
 * @author pony
 * @date 2017/10/24
 * 先实现一些基本的功能，以后如果需要兼容jdbc，可扩展为ResultSetMetadata
 * column 从1开始
 */
//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
//@JsonSubTypes({
//        @JsonSubTypes.Type(SwiftMetaDataBean.class)
//})
public interface SwiftMetaData {
    SwiftDatabase getSwiftDatabase();

    String getSchemaName() throws SwiftMetaDataException;

    String getTableName() throws SwiftMetaDataException;

    String getRemark() throws SwiftMetaDataException;

    int getColumnCount() throws SwiftMetaDataException;

    String getColumnName(int index) throws SwiftMetaDataException;

    String getColumnRemark(int index) throws SwiftMetaDataException;

    /**
     * sql type
     *
     * @param index 序号
     * @return 字段对应的sql type
     * @throws SwiftMetaDataException 异常
     * @see java.sql.Types
     */
    int getColumnType(int index) throws SwiftMetaDataException;

    /**
     * 字段长度
     *
     * @param index 序号
     * @return 字段长度
     * @throws SwiftMetaDataException 异常
     */
    int getPrecision(int index) throws SwiftMetaDataException;

    /**
     * 字段小数位数
     *
     * @param index 序号
     * @return 字段小数位数
     * @throws SwiftMetaDataException 异常
     */
    int getScale(int index) throws SwiftMetaDataException;

    SwiftMetaDataColumn getColumn(int index) throws SwiftMetaDataException;

    SwiftMetaDataColumn getColumn(String columnName) throws SwiftMetaDataException;

    int getColumnIndex(String columnName) throws SwiftMetaDataException;

    String getColumnId(int index) throws SwiftMetaDataException;

    String getColumnId(String columnName) throws SwiftMetaDataException;

    List<String> getFieldNames();

    String getId();
}