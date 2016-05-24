package com.fr.bi.conf.report.widget;

import com.finebi.cube.conf.field.BIBusinessField;

import java.io.Serializable;

/**
 * 用于分析的原始字段， _src, 业务包字段的子类
 * Created by GUY on 2015/3/30.
 */
public class BIDataColumn extends BIBusinessField implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1869519458089727759L;


    public BIDataColumn() {
        super();
    }

    public BIDataColumn(BIBusinessField fieldKey) {
        super(fieldKey.getTableBelongTo(), fieldKey.getFieldName(), fieldKey.getClassType(), fieldKey.getFieldSize());
    }


    @Override
    public boolean equals(Object obj) {
        return obj instanceof BIDataColumn
                && super.equals(obj);
    }


}