package com.fr.bi.field.dimension.dimension;

import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.field.dimension.calculator.DateDimensionCalculator;
import com.fr.bi.stable.engine.index.key.IndexTypeKey;
import com.fr.bi.stable.report.result.DimensionCalculator;

import java.util.List;

public class BIDateDimension extends BIAbstractDimension {

    private static final long serialVersionUID = 45713218397075589L;

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

    @Override
    public Object getValueByType(Object data) {
        return data == null || !(data instanceof Number) ? null : Long.parseLong(data.toString());
    }
}