package com.fr.swift.base.meta;

import com.fr.swift.source.SwiftMetaDataColumn;

import java.io.Serializable;

/**
 * @Author: Lucifer
 * @Description:
 * @Date: Created in 2018-3-8
 */
public class MetaDataColumnBean implements SwiftMetaDataColumn, Serializable {
    private static final int DEFAULT_PRECISION = 255;
    private static final int DEFAULT_SCALE = 15;
    private static final long serialVersionUID = 5094076095250338803L;

    private int type;
    private String name;
    private String remark;
    private int precision;
    private int scale;
    private String columnId;

    public MetaDataColumnBean() {
    }

    public MetaDataColumnBean(String name, int sqlType) {
        this(name, null, sqlType, DEFAULT_PRECISION, DEFAULT_SCALE);
    }

    public MetaDataColumnBean(String name, String remark, int sqlType) {
        this(name, remark, sqlType, DEFAULT_PRECISION, DEFAULT_SCALE);
    }

    public MetaDataColumnBean(String name, String remark, int sqlType, String columnId) {
        this(name, remark, sqlType, DEFAULT_PRECISION, DEFAULT_SCALE, columnId);
    }

    public MetaDataColumnBean(String name, int sqlType, int size) {
        this(name, null, sqlType, size, DEFAULT_SCALE);
    }

    public MetaDataColumnBean(String name, int sqlType, int size, String columnId) {
        this(name, null, sqlType, size, DEFAULT_SCALE, columnId);
    }

    public MetaDataColumnBean(String name, int sqlType, int size, int scale) {
        this(name, null, sqlType, size, scale);
    }

    public MetaDataColumnBean(String name, String remark, int sqlType, int precision, int scale) {
        this.type = sqlType;
        this.name = name;
        this.remark = remark;
        this.precision = precision;
        this.scale = scale;
        this.columnId = name;
    }

    public MetaDataColumnBean(String name, String remark, int sqlType, int precision, int scale, String columnId) {
        this.type = sqlType;
        this.name = name;
        this.remark = remark;
        this.precision = precision;
        this.scale = scale;
        this.columnId = columnId;
    }

    @Override
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    @Override
    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    @Override
    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    @Override
    public String toString() {
        return "{" + type + ", " + name + "}";
    }
}
