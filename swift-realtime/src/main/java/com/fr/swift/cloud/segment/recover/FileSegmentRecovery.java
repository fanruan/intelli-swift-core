package com.fr.swift.cloud.segment.recover;

import com.fr.swift.cloud.beans.annotation.SwiftBean;
import com.fr.swift.cloud.bitmap.impl.AllShowBitMap;
import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.segment.Segment;
import com.fr.swift.cloud.segment.SegmentKey;
import com.fr.swift.cloud.segment.operator.Inserter;
import com.fr.swift.cloud.segment.operator.insert.SwiftInserter;

import java.util.List;

/**
 * @author anchore
 * @date 2018/5/23
 */
@SwiftBean(name = "segmentRecovery")
public class FileSegmentRecovery extends AbstractSegmentRecovery {

    @Override
    public void recover(List<SegmentKey> segmentKeys) {
        for (SegmentKey segKey : segmentKeys) {
            recover(segKey);
        }
    }

    private void recover(SegmentKey segKey) {
        Segment realtimeSeg = null;
        SegmentBackupResultSet resultSet;
        Inserter inserter;
        try {
            realtimeSeg = segmentService.getSegment(segKey);
            inserter = SwiftInserter.ofOverwriteMode(realtimeSeg);
            resultSet = new SegmentBackupResultSet(getBackupSegment(segKey, realtimeSeg.getMetaData()));
            inserter.insertData(resultSet);
            realtimeSeg.putAllShowIndex(resultSet.getAllShowIndex());
            SwiftLoggers.getLogger().info("{} recover {} rows success", segKey, realtimeSeg.getRowCount());
        } catch (Exception e) {
            SwiftLoggers.getLogger().warn("{} recover failed, caused by {}", segKey, e);
            if (realtimeSeg != null) {
                realtimeSeg.putRowCount(0);
                realtimeSeg.putAllShowIndex(AllShowBitMap.of(0));
            }
        }
    }
}