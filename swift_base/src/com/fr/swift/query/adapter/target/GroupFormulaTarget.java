package com.fr.swift.query.adapter.target;

import com.fr.swift.query.adapter.AbstractQueryColumn;

/**
 * Created by pony on 2017/12/26.
 */
public class GroupFormulaTarget extends AbstractQueryColumn implements GroupTarget {
    public GroupFormulaTarget(int index) {
        super(index);
    }

    @Override
    public TargetDeep getTargetDeep() {
        return TargetDeep.ROW;
    }
}
