package com.fr.swift.config.unique;

import com.fr.config.holder.Conf;
import com.fr.config.holder.factory.Holders;
import com.fr.config.utils.UniqueKey;
import com.fr.stable.StringUtils;
import com.fr.swift.source.SwiftMetaDataColumn;

/**
 * @Author: Lucifer
 * @Description:
 * @Date: Created in 2018-3-8
 */
public class MetaDataColumnUnique extends UniqueKey implements SwiftMetaDataColumn {
    private Conf<Integer> type = Holders.simple(0);

    private Conf<String> name = Holders.simple(StringUtils.EMPTY);

    private Conf<String> remark = Holders.simple(StringUtils.EMPTY);

    private Conf<Integer> precision = Holders.simple(0);

    private Conf<Integer> scale = Holders.simple(0);

    private Conf<String> columnId = Holders.simple(StringUtils.EMPTY);

    public MetaDataColumnUnique() {
    }

    public MetaDataColumnUnique(SwiftMetaDataColumn columnMeta) {
        this(columnMeta.getType(), columnMeta.getName(), columnMeta.getRemark(), columnMeta.getPrecision(), columnMeta.getScale(), columnMeta.getId());
    }

    public MetaDataColumnUnique(int type, String name, String remark, int precision, int scale, String columnId) {
        this.type.set(type);
        this.name.set(name);
        this.remark.set(remark);
        this.precision.set(precision);
        this.scale.set(scale);
        this.columnId.set(columnId);
    }

    @Override
    public int getType() {
        return type.get();
    }

    @Override
    public String getName() {
        return name.get();
    }

    @Override
    public String getRemark() {
        return remark.get();
    }

    @Override
    public int getPrecision() {
        return precision.get();
    }

    @Override
    public int getScale() {
        return scale.get();
    }

    @Override
    public String getId() {
        return columnId.get();
    }
}