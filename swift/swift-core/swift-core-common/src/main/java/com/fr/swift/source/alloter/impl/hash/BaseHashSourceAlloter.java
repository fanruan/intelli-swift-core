package com.fr.swift.source.alloter.impl.hash;

import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.impl.BaseSourceAlloter;

/**
 * @author anchore
 * @date 2018/12/30
 */
abstract class BaseHashSourceAlloter extends BaseSourceAlloter<HashAllotRule, HashRowInfo> {

    protected BaseHashSourceAlloter(SourceKey tableKey, HashAllotRule rule) {
        super(tableKey, rule);
    }

    @Override
    protected int getLogicOrder(HashRowInfo rowInfo) {
        return Math.abs(rowInfo.getRow().getValue(rule.getFieldIndex()).hashCode() % rule.getSegCount());
    }
}