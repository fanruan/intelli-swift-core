package com.fr.swift.segment.recover;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentResultSet;

/**
 * @author anchore
 * @date 2018/6/4
 */
class SegmentBackupResultSet extends SegmentResultSet {
    SegmentBackupResultSet(Segment seg) {
        super(seg);
    }

    public ImmutableBitMap getAllShowIndex() {
        return seg.getAllShowIndex();
    }
}