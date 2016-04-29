package com.fr.bi.field.dimension.dimension;

import com.fr.bi.base.key.BIKey;
import com.fr.bi.conf.report.widget.BIDataColumn;
import com.fr.bi.field.dimension.calculator.DateDimensionCalculator;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.engine.index.key.IndexTypeKey;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.fr.bi.stable.report.result.DimensionCalculator;

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
        if (v != null) {
            return v.toString();
        }
        return "";
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
    public BIKey createKey(BIField column) {
        return new IndexTypeKey(column.getFieldName(), group.getType());
    }

    @Override
    public DimensionCalculator createCalculator(BIDataColumn column, List<BITableSourceRelation> relations) {
        return new DateDimensionCalculator(this, column, relations);
    }
}