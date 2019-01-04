package com.fr.swift.source.alloter.impl.hash;

import com.fr.swift.source.SourceKey;

/**
 * @author anchore
 * @date 2018/12/21
 */
public class RealtimeHashSourceAlloter extends BaseHashSourceAlloter {

    protected RealtimeHashSourceAlloter(SourceKey tableKey, HashAllotRule rule) {
        super(tableKey, rule);
    }

    @Override
    protected SegmentState getInsertableSeg() {
        // TODO: 2018/12/26
        return null;
    }
}