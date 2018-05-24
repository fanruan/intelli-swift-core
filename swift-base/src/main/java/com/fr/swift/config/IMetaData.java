package com.fr.swift.config;

import java.util.List;

/**
 * @Author: Lucifer
 * @Description:
 * @Date: Created in 2018-3-8
 */
public interface IMetaData<T extends IMetaDataColumn> {
    String getSchema();

    void setSchema(String schema);

    String getTableName();

    void setTableName(String tableName);

    String getRemark();

    void setRemark(String remark);

    List<T> getFields();

    void setFields(List<T> fields);
}
