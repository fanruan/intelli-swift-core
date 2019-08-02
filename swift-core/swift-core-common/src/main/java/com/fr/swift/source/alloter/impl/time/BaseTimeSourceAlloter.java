package com.fr.swift.source.alloter.impl.time;

import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.impl.BaseSourceAlloter;

/**
 * @author Marvin.Zhao
 * @date 2019/7/17
 */
abstract class BaseTimeSourceAlloter extends BaseSourceAlloter<TimeAllotRule, TimeRowInfo> {

    protected BaseTimeSourceAlloter(SourceKey tableKey, TimeAllotRule rule) {
        super(tableKey, rule);
    }

    @Override
    protected int getLogicOrder(TimeRowInfo rowInfo) {
        Object key = rowInfo.getRow().getValue(rule.getFieldIndex());
        int index = rule.getHashFunction().indexOf(key);
        return index;
    }
}