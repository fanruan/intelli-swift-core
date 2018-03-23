package com.fr.swift.segment;

import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.impl.base.FakeStringDetailColumn;
import com.fr.swift.util.Crasher;

import java.util.List;

/**
 * This class created on 2018-1-10 10:54:44
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class RealtimeSegmentHolder extends AbstractSegmentHolder {

    public RealtimeSegmentHolder(Segment segment) throws SwiftMetaDataException {
        super(segment);
    }

    @Override
    public void release() {
    }

    @Override
    public void release(List<ColumnKey> columns) {
    }
}
