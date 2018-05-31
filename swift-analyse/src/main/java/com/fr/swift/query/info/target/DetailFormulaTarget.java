package com.fr.swift.query.info.target;

import com.fr.swift.query.info.dimension.AbstractQueryColumn;
import com.fr.swift.query.info.target.cal.CalTargetType;

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
