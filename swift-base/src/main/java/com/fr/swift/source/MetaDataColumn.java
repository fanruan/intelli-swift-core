package com.fr.swift.source;

/**
 * 保存列的信息
 */
public class MetaDataColumn implements SwiftMetaDataColumn {
    private static final long serialVersionUID = -3638876557665551219L;

    private static final int DEFAULT_PRECISION = 255;

    private static final int DEFAULT_SCALE = 15;

    private String name;

    private String remark;

    private String columnId;

    private int type;

    private int precision;

    private int scale;

    public MetaDataColumn(String name, int sqlType) {
        this(name, null, sqlType, DEFAULT_PRECISION, DEFAULT_SCALE);
    }

    public MetaDataColumn(String name, String remark, int sqlType) {
        this(name, remark, sqlType, DEFAULT_PRECISION, DEFAULT_SCALE);
    }

    public MetaDataColumn(String name, String remark, int sqlType, String columnId) {
        this(name, remark, sqlType, DEFAULT_PRECISION, DEFAULT_SCALE, columnId);
    }

    public MetaDataColumn(String name, int sqlType, int size) {
        this(name, null, sqlType, size, DEFAULT_SCALE);
    }

    public MetaDataColumn(String name, int sqlType, int size, String columnId) {
        this(name, null, sqlType, size, DEFAULT_SCALE, columnId);
    }

    public MetaDataColumn(String name, int sqlType, int size, int scale) {
        this(name, null, sqlType, size, scale);
    }

    public MetaDataColumn(String name, String remark, int type, int precision, int scale) {
        this(name, remark, type, precision, scale, name);
    }

    public MetaDataColumn(String name, String remark, int type, int precision, int scale, String columnId) {
        this.name = name;
        this.remark = remark;
        this.columnId = columnId;
        this.type = type;
        this.precision = precision;
        this.scale = scale;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return columnId;
    }

    @Override
    public String getRemark() {
        return remark;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public int getPrecision() {
        return precision;
    }

    @Override
    public int getScale() {
        return scale;
    }

    /**
     * 克隆方法
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return "{" + type + ", " + name + "}";
    }
}