package com.fr.swift.source.alloter.impl.line;

import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.SegmentInfo;
import com.fr.swift.source.alloter.impl.BaseSourceAlloter;

/**
 * @author anchore
 * @date 2018/12/21
 */
public class RealtimeLineSourceAlloter extends BaseSourceAlloter<LineAllotRule, LineRowInfo> {

    public RealtimeLineSourceAlloter(SourceKey tableKey, LineAllotRule rule) {
        super(tableKey, rule);
    }

    @Override
    public SegmentInfo allot(LineRowInfo rowInfo) {
        // 目前想的分到一个最接近满的seg，然后分新块
        return null;
    }
}