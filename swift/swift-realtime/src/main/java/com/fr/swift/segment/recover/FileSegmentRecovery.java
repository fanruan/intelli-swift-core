package com.fr.swift.segment.recover;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.bitmap.impl.AllShowBitMap;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.segment.operator.insert.SwiftInserter;
import com.fr.swift.util.IoUtil;

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
        SegmentBackupResultSet resultSet = null;
        Inserter inserter = null;
        try {
            realtimeSeg = newRealtimeSegment(localSegmentProvider.getSegment(segKey));
            inserter = new SwiftInserter(realtimeSeg);
            resultSet = new SegmentBackupResultSet(getBackupSegment(segKey, realtimeSeg.getMetaData()));
            inserter.importData(resultSet);
            IoUtil.release(inserter);
            realtimeSeg.putAllShowIndex(resultSet.getAllShowIndex());
        } catch (Exception e) {
            IoUtil.release(inserter);
            SwiftLoggers.getLogger().warn("{} recover failed, caused by {}", segKey, e);
            if (realtimeSeg != null) {
                realtimeSeg.putRowCount(0);
                realtimeSeg.putAllShowIndex(AllShowBitMap.of(0));
            }
        } finally {
            IoUtil.close(resultSet);
            SegmentUtils.release(realtimeSeg);
        }
    }
}