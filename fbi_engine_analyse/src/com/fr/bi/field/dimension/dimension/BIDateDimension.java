package com.fr.bi.field.dimension.dimension;

import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.field.dimension.calculator.DateDimensionCalculator;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.engine.index.key.IndexTypeKey;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.stable.StringUtils;

import java.util.List;

public class BIDateDimension extends BIAbstractDimension {

    /**
     * 转化string
     *
     * @param v 值
     * @return 转化的string
     */
    @Override
    public String toString(Object v) {
        if (v == null){
            return StringUtils.EMPTY;
        }
        if (group.getType() == BIReportConstant.GROUP.M) {
            return String.valueOf(((Number)v).intValue() + 1);
        }
        return v.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BIDateDimension)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return true;
    }


    @Override
    public BIKey createKey(BusinessField column) {
        return new IndexTypeKey(column.getFieldName(), group.getType());
    }

    @Override
    public DimensionCalculator createCalculator(BusinessField column, List<BITableSourceRelation> relations) {
        return new DateDimensionCalculator(this, column, relations);
    }

    @Override
    public DimensionCalculator createCalculator(BusinessField column, List<BITableSourceRelation> relations, List<BITableSourceRelation> directToDimensionRelations) {
        return new DateDimensionCalculator(this, column, relations, directToDimensionRelations);
    }

    private Object insertZero(int time) {
        if (time < 10) {
            return "0" + time;
        }
        return "" + time;
    }

    @Override
    public Object getValueByType(Object data) {
        return data == null ? null : Long.parseLong(data.toString());
    }
}