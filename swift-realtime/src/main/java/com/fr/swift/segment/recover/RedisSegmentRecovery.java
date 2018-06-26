package com.fr.swift.segment.recover;

import com.fr.swift.db.Table;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.operator.Inserter;

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
        try {
            for (SegmentKey segKey : segmentKeys) {
                Table table = SwiftDatabase.getInstance().getTable(segKey.getTable());
                Segment realtimeSeg = newRealtimeSegment(localSegmentProvider.getSegment(segKey));
                Inserter insert = operators.getInserter(table, realtimeSeg);
                insert.insertData(new RedisBackupResultSet(getBackupSegment(realtimeSeg)));
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }
}
