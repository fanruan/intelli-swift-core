package com.fr.swift.source.alloter.impl.hash;

import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.SegmentInfo;
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
    public SegmentInfo allot(HashRowInfo rowInfo) {
        // 目前想的优先分到还在内存里，最接近满的seg，然后才是新块
        return null;
    }
}