package com.fr.swift.config;

/**
 * @Author: Lucifer
 * @Description:
 * @Date: Created in 2018-3-8
 */
public interface IMetaDataColumn {

    int getType();

    void setType(int type);

    String getName();

    void setName(String name);

    String getRemark();

    void setRemark(String remark);

    int getPrecision();

    void setPrecision(int precision);

    int getScale();

    void setScale(int scale);

    String getColumnId();

    void setColumnId(String columnId);
}
