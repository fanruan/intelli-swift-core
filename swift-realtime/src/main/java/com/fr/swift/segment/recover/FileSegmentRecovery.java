package com.fr.swift.segment.recover;

import com.fr.swift.bitmap.impl.EmptyBitmap;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.segment.operator.insert.SwiftInserter;

import java.util.List;

/**
 * @author anchore
 * @date 2018/5/23
 */
public class FileSegmentRecovery extends AbstractSegmentRecovery {

    @Override
    public void recover(List<SegmentKey> segmentKeys) {
        for (SegmentKey segKey : segmentKeys) {
            recover(segKey);
        }
    }

    private void recover(SegmentKey segKey) {
        Segment realtimeSeg = null;
        try {
            realtimeSeg = newRealtimeSegment(localSegmentProvider.getSegment(segKey));
            Inserter inserter = new SwiftInserter(realtimeSeg);
            SegmentBackupResultSet resultSet = new SegmentBackupResultSet(getBackupSegment(realtimeSeg));
            inserter.insertData(resultSet);
            realtimeSeg.putAllShowIndex(resultSet.getAllShowIndex());
        } catch (Exception e) {
            SwiftLoggers.getLogger().warn("{} recover failed, caused by {}", segKey, e.getMessage());
            if (realtimeSeg != null) {
                realtimeSeg.putRowCount(0);
                realtimeSeg.putAllShowIndex(new EmptyBitmap());
            }
        }
    }
}