package com.fr.swift.config.pojo;

import com.fr.swift.config.IMetaDataColumn;

/**
 * @Author: Lucifer
 * @Description:
 * @Date: Created in 2018-3-8
 */
public class MetaDataColumnPojo implements IMetaDataColumn {
    private int type;
    private String name;
    private String remark;
    private int precision;
    private int scale;
    private boolean isAdd = false;

    public MetaDataColumnPojo(int type, String name, String remark, int precision, int scale) {
        this.type = type;
        this.name = name;
        this.remark = remark;
        this.precision = precision;
        this.scale = scale;
    }

    public MetaDataColumnPojo(int type, String name, String remark, int precision, int scale, boolean isAdd) {
        this.type = type;
        this.name = name;
        this.remark = remark;
        this.precision = precision;
        this.scale = scale;
        this.isAdd = isAdd;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getRemark() {
        return remark;
    }

    @Override
    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public int getPrecision() {
        return precision;
    }

    @Override
    public void setPrecision(int precision) {
        this.precision = precision;
    }

    @Override
    public int getScale() {
        return scale;
    }

    @Override
    public void setScale(int scale) {
        this.scale = scale;
    }

    @Override
    public boolean isAdd() {
        return isAdd;
    }

    @Override
    public void setAdd(boolean add) {
        isAdd = add;
    }
}
