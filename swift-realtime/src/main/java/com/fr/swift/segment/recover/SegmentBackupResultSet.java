package com.fr.swift.segment.recover;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentResultSet;
import com.fr.swift.segment.column.DetailColumn;

/**
 * @author anchore
 * @date 2018/6/4
 */
class SegmentBackupResultSet extends SegmentResultSet {
    SegmentBackupResultSet(Segment seg) {
        super(seg, false);
    }

    public ImmutableBitMap getAllShowIndex() {
        return allShowIndex;
    }

    @Override
    protected <T> T getDetail(DetailColumn<T> detail, int cursor) {
        try {
            return super.getDetail(detail, cursor);
        } catch (Exception e) {
            return null;
        }
    }
}