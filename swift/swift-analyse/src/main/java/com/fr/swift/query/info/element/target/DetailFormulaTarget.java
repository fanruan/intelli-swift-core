package com.fr.swift.query.info.element.target;

import com.fr.swift.query.info.bean.type.cal.CalTargetType;
import com.fr.swift.query.info.element.dimension.AbstractQueryColumn;

/**
 * Created by pony on 2017/12/22.
 */
public class DetailFormulaTarget extends AbstractQueryColumn implements DetailTarget {

    public DetailFormulaTarget(int index) {
        super(index);
    }

    @Override
    public int[] paramIndexes() {
        return new int[0];
    }

    @Override
    public int resultIndex() {
        return 0;
    }

    @Override
    public CalTargetType type() {
        return CalTargetType.FORMULA;
    }
}
