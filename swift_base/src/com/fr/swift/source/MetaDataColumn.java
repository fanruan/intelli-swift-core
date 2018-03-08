package com.fr.swift.source;

import com.fr.swift.config.pojo.MetaDataColumnPojo;

/**
 * 保存列的信息
 */
public class MetaDataColumn implements SwiftMetaDataColumn {
    private static final long serialVersionUID = -3638876557665551219L;
    private static final int DEFAULT_PRECISION = 255;
    private static final int DEFAULT_SCALE = 15;

    private MetaDataColumnPojo metaDataColumnPojo;

    public MetaDataColumn(String name, int sqlType) {
        this(name, null, sqlType, DEFAULT_PRECISION, DEFAULT_SCALE);
    }

    public MetaDataColumn(String name, String remark, int sqlType) {
        this(name, remark, sqlType, DEFAULT_PRECISION, DEFAULT_SCALE);
    }

    public MetaDataColumn(String name, int sqlType, int size) {
        this(name, null, sqlType, size, DEFAULT_SCALE);
    }

    public MetaDataColumn(String name, int sqlType, int size, int scale) {
        this(name, null, sqlType, size, scale);
    }


    public MetaDataColumn(String name, String remark, int sqlType, int precision, int scale) {
        metaDataColumnPojo = new MetaDataColumnPojo(sqlType, name, remark, precision, scale);
    }

    @Override
    public int getPrecision() {
        return metaDataColumnPojo.getPrecision();
    }

    @Override
    public int getType() {
        return metaDataColumnPojo.getType();
    }

    @Override
    public String getName() {
        return metaDataColumnPojo.getName();
    }

    @Override
    public String getRemark() {
        return metaDataColumnPojo.getRemark();
    }

    @Override
    public int getScale() {
        return metaDataColumnPojo.getScale();
    }

    @Override
    public MetaDataColumnPojo getMetaDataColumnPojo() {
        return this.metaDataColumnPojo;
    }

    /**
     * 克隆方法
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
