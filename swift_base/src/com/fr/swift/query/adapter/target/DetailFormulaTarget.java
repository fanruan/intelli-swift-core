package com.fr.swift.query.adapter.target;

import com.fr.swift.query.adapter.AbstractQueryColumn;
import com.fr.swift.query.adapter.target.cal.CalTargetType;
import com.fr.swift.query.filter.info.FilterInfo;

/**
 * Created by pony on 2017/12/22.
 */
public class DetailFormulaTarget extends AbstractQueryColumn implements DetailTarget {

    public DetailFormulaTarget(int index) {
        super(index);
    }

    @Override
    public FilterInfo getFilter() {
        return null;
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
        return null;
    }
}
