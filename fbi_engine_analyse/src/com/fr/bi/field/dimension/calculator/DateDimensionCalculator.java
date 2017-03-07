package com.fr.bi.field.dimension.calculator;

import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.BIReportConstant;

import java.util.Comparator;
import java.util.List;

/**
 * Created by 小灰灰 on 2015/6/30.
 */
public class DateDimensionCalculator extends AbstractDimensionCalculator {
    private static final long serialVersionUID = -1201531041684245593L;

    public DateDimensionCalculator(BIDimension dimension, BusinessField column, List<BITableSourceRelation> relations) {
        super(dimension, column, relations);
    }

    public DateDimensionCalculator(BIDimension dimension, BusinessField field, List<BITableSourceRelation> relations, List<BITableSourceRelation> directToDimensionRelations) {
        super(dimension, field, relations, directToDimensionRelations);
    }

    public int getGroupDate() {
        return getGroup().getType();
    }

    @Override
    public Comparator getComparator() {
        if (getSortType() == BIReportConstant.SORT.NUMBER_DESC) {
            return BIBaseConstant.COMPARATOR.COMPARABLE.DESC;
        } else{
            return BIBaseConstant.COMPARATOR.COMPARABLE.ASC;
        }
    }
}