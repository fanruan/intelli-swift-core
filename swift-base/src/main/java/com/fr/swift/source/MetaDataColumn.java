package com.fr.swift.source;

import com.fr.swift.config.conf.bean.MetaDataColumnBean;

/**
 * 保存列的信息
 */
public class MetaDataColumn implements SwiftMetaDataColumn {
    private static final long serialVersionUID = -3638876557665551219L;
    private static final int DEFAULT_PRECISION = 255;
    private static final int DEFAULT_SCALE = 15;

    private MetaDataColumnBean metaDataColumnBean;

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


    public MetaDataColumn(String name, String remark, int sqlType, int precision, int scale) {
        metaDataColumnBean = new MetaDataColumnBean(sqlType, name, remark, precision, scale);
    }

    public MetaDataColumn(String name, String remark, int sqlType, int precision, int scale, String columnId) {
        this.metaDataColumnBean = new MetaDataColumnBean(sqlType, name, remark, precision, scale, columnId);
    }

    @Override
    public int getPrecision() {
        return metaDataColumnBean.getPrecision();
    }

    @Override
    public int getType() {
        return metaDataColumnBean.getType();
    }

    @Override
    public String getName() {
        return metaDataColumnBean.getName();
    }

    @Override
    public String getRemark() {
        return metaDataColumnBean.getRemark();
    }

    @Override
    public int getScale() {
        return metaDataColumnBean.getScale();
    }

    @Override
    public MetaDataColumnBean getMetaDataColumnBean() {
        return this.metaDataColumnBean;
    }

    /**
     * 克隆方法
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String getColumnId() {
        return metaDataColumnBean.getColumnId();
    }

    @Override
    public String toString() {
        return metaDataColumnBean.toString();
    }
}
