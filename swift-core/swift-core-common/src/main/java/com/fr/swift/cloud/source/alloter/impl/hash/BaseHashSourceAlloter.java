package com.fr.swift.cloud.source.alloter.impl.hash;

import com.fr.swift.cloud.source.SourceKey;
import com.fr.swift.cloud.source.alloter.impl.BaseSourceAlloter;

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
        Object key = rowInfo.getRow().getValue(rule.getFieldIndexes()[0]);
        return rule.getHashFunction().indexOf(key);
    }
}