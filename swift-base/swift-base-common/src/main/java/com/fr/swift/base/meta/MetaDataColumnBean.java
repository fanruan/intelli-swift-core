package com.fr.swift.base.meta;

import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.util.Assert;

import java.io.Serializable;
import java.sql.Types;

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

    /**
     * @deprecated 换静态工厂或Builder
     */
    @Deprecated
    public MetaDataColumnBean(String name, int sqlType) {
        this(name, null, sqlType, DEFAULT_PRECISION, DEFAULT_SCALE);
    }

    /**
     * @deprecated 换静态工厂或Builder
     */
    @Deprecated
    public MetaDataColumnBean(String name, String remark, int sqlType, String columnId) {
        this(name, remark, sqlType, DEFAULT_PRECISION, DEFAULT_SCALE, columnId);
    }

    /**
     * @deprecated 换静态工厂或Builder
     */
    @Deprecated
    public MetaDataColumnBean(String name, int sqlType, int size) {
        this(name, null, sqlType, size, DEFAULT_SCALE);
    }

    /**
     * @deprecated 换静态工厂或Builder
     */
    @Deprecated
    public MetaDataColumnBean(String name, String remark, int sqlType, int precision, int scale) {
        this.type = sqlType;
        this.name = name;
        this.remark = remark;
        this.precision = precision;
        this.scale = scale;
        this.columnId = name;
    }

    /**
     * @deprecated 换静态工厂或Builder
     */
    @Deprecated
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

    public static MetaDataColumnBean ofInt(String name) {
        return new Builder().setName(name).setType(Types.INTEGER)
                .setPrecision(10).setColumnId(name).build();
    }

    public static MetaDataColumnBean ofLong(String name) {
        return new Builder().setName(name).setType(Types.BIGINT)
                .setPrecision(19).setColumnId(name).build();
    }

    public static MetaDataColumnBean ofDouble(String name) {
        return new Builder().setName(name).setType(Types.DOUBLE)
                .setPrecision(22).setScale(15).setColumnId(name).build();
    }

    public static MetaDataColumnBean ofString(String name, int length) {
        return new Builder().setName(name).setType(Types.VARCHAR)
                .setPrecision(length).setColumnId(name).build();
    }

    public static MetaDataColumnBean ofString(String name) {
        return ofString(name, 255);
    }

    public static MetaDataColumnBean ofDate(String name) {
        return new Builder().setName(name).setType(Types.DATE)
                .setColumnId(name).build();
    }

    public static class Builder {
        private MetaDataColumnBean columnMeta = new MetaDataColumnBean();

        public Builder() {
        }

        public Builder(SwiftMetaDataColumn columnMeta) {
            this.columnMeta.name = columnMeta.getName();
            this.columnMeta.type = columnMeta.getType();
            this.columnMeta.precision = columnMeta.getPrecision();
            this.columnMeta.scale = columnMeta.getScale();
            this.columnMeta.columnId = columnMeta.getColumnId();
            this.columnMeta.remark = columnMeta.getRemark();
        }

        public Builder setName(String name) {
            columnMeta.name = name;
            return this;
        }

        public Builder setType(int type) {
            columnMeta.type = type;
            return this;
        }

        public Builder setPrecision(int precision) {
            columnMeta.precision = precision;
            return this;
        }

        public Builder setScale(int scale) {
            columnMeta.scale = scale;
            return this;
        }

        public Builder setColumnId(String columnId) {
            columnMeta.columnId = columnId;
            return this;
        }

        public Builder setRemark(String remark) {
            columnMeta.remark = remark;
            return this;
        }

        public MetaDataColumnBean build() {
            Assert.hasText(columnMeta.name);
            Assert.hasText(columnMeta.columnId);
            Assert.isTrue(columnMeta.precision >= 0);
            Assert.isTrue(columnMeta.scale >= 0);
            return columnMeta;
        }
    }
}
