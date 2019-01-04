package com.fr.swift.source.alloter.impl.line;

import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.impl.BaseSourceAlloter;

/**
 * @author anchore
 * @date 2018/12/30
 */
abstract class BaseLineSourceAlloter extends BaseSourceAlloter<LineAllotRule, LineRowInfo> {

    protected BaseLineSourceAlloter(SourceKey tableKey, LineAllotRule rule) {
        super(tableKey, rule);
    }

    @Override
    protected int getLogicOrder(LineRowInfo rowInfo) {
        return (int) (rowInfo.getCursor() / rule.getCapacity());
    }
}