package com.fr.swift.segment.recovery;

import com.fr.swift.bitmap.impl.AllShowBitMap;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.segment.operator.insert.SwiftInserter;
import com.fr.swift.segment.recover.AbstractSegmentRecovery;

import java.util.List;

/**
 * This class created on 2018/6/22
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class RedisSegmentRecovery extends AbstractSegmentRecovery {
    @Override
    public void recover(List<SegmentKey> segmentKeys) {
        for (SegmentKey segKey : segmentKeys) {
            recover(segKey);
        }
    }

    private void recover(SegmentKey segKey) {
        Segment realtimeSeg = null;
        RedisBackupResultSet resultSet = null;
        Inserter inserter = null;
        try {
            realtimeSeg = newRealtimeSegment(localSegmentProvider.getSegment(segKey));
            inserter = new SwiftInserter(realtimeSeg);
            resultSet = new RedisBackupResultSet(getBackupSegment(segKey, realtimeSeg.getMetaData()));
            inserter.insertData(resultSet);
            realtimeSeg.putAllShowIndex(resultSet.getAllShowIndex());
        } catch (Exception e) {
            SwiftLoggers.getLogger().warn("{} recover failed", segKey, e);
            if (realtimeSeg != null) {
                realtimeSeg.putRowCount(0);
                realtimeSeg.putAllShowIndex(AllShowBitMap.of(0));
            }
        }
    }
}
