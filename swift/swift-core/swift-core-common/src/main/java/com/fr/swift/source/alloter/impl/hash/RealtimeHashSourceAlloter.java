package com.fr.swift.source.alloter.impl.hash;

import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.impl.BaseSourceAlloter;

/**
 * @author anchore
 * @date 2018/12/21
 */
public class RealtimeHashSourceAlloter extends BaseSourceAlloter<HashAllotRule, HashRowInfo> {

    protected RealtimeHashSourceAlloter(SourceKey tableKey, HashAllotRule rule) {
        super(tableKey, rule);
    }

    @Override
    protected SegmentState append(int logicOrder) {
        // TODO: 2018/12/26
        return null;
    }

    @Override
    protected int getLogicOrder(HashRowInfo rowInfo) {
        // TODO: 2018/12/26
        return 0;
    }
}