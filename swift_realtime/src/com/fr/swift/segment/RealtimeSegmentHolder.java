package com.fr.swift.segment;

import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.segment.column.impl.base.FakeStringDetailColumn;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.Crasher;

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
        try {
            for (int i = 1, len = metaData.getColumnCount(); i <= len; i++) {
                if (getColumn(metaData.getColumnName(i)) instanceof FakeStringDetailColumn) {
//                    segment.getColumn(new ColumnKey(metaData.getColumnName(i))).getBitmapIndex().release();
                    getColumn(metaData.getColumnName(i)).release();
                }
            }
        } catch (Exception e) {
            Crasher.crash(e);
        } finally {
//            segment.release();
        }
    }
}
