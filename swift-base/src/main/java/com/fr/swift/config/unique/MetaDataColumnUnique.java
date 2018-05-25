package com.fr.swift.config.unique;

import com.fr.config.holder.Conf;
import com.fr.config.holder.factory.Holders;
import com.fr.config.utils.UniqueKey;
import com.fr.stable.StringUtils;
import com.fr.swift.config.IMetaDataColumn;

/**
 * @Author: Lucifer
 * @Description:
 * @Date: Created in 2018-3-8
 */
public class MetaDataColumnUnique extends UniqueKey implements IMetaDataColumn {

    private final static String NAMESPACE = "metadata_column";

    private Conf<Integer> type = Holders.simple(0);
    private Conf<String> name = Holders.simple(StringUtils.EMPTY);
    private Conf<String> remark = Holders.simple(StringUtils.EMPTY);
    private Conf<Integer> precision = Holders.simple(0);
    private Conf<Integer> scale = Holders.simple(0);
    private Conf<String> columnId = Holders.simple(StringUtils.EMPTY);

    public MetaDataColumnUnique() {
    }

    public MetaDataColumnUnique(int type, String name, String remark, int precision, int scale, String columnId) {
        this.setType(type);
        this.setName(name);
        this.setRemark(remark);
        this.setPrecision(precision);
        this.setScale(scale);
        this.setColumnId(columnId);
    }

    @Override
    public int getType() {
        return type.get();
    }

    @Override
    public void setType(int type) {
        this.type.set(type);
    }

    @Override
    public String getName() {
        return name.get();
    }

    @Override
    public void setName(String name) {
        this.name.set(name);
    }

    @Override
    public String getRemark() {
        return remark.get();
    }

    @Override
    public void setRemark(String remark) {
        this.remark.set(remark);
    }

    @Override
    public int getPrecision() {
        return precision.get();
    }

    @Override
    public void setPrecision(int precision) {
        this.precision.set(precision);
    }

    @Override
    public int getScale() {
        return scale.get();
    }

    @Override
    public void setScale(int scale) {
        this.scale.set(scale);
    }

    @Override
    public String getColumnId() {
        return columnId.get();
    }

    @Override
    public void setColumnId(String columnId) {
        this.columnId.set(columnId);
    }
}
