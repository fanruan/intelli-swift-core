package com.fr.swift.segment.recover;

import com.fr.swift.db.Table;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.source.SwiftResultSet;

import java.util.List;

/**
 * @author anchore
 * @date 2018/5/23
 */
public class FileSegmentRecovery extends AbstractSegmentRecovery {

    @Override
    public void recover(List<SegmentKey> segmentKeys) {
        try {
            for (SegmentKey segKey : segmentKeys) {
                Table table = SwiftDatabase.getInstance().getTable(segKey.getTable());
                Segment realtimeSeg = newRealtimeSegment(localSegmentProvider.getSegment(segKey));
                Inserter insert = operators.getInserter(table, realtimeSeg);
                SwiftResultSet resultSet = new HisSegBackupResultSet(getBackupSegment(realtimeSeg));
                insert.insertData(resultSet);
                realtimeSeg.putAllShowIndex(((HisSegBackupResultSet) resultSet).getAllShowIndex());
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }
}