package com.fr.swift.segment.operator.delete;

import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.db.impl.SwiftDatabase.Schema;
import com.fr.swift.segment.HistorySegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.Interval;

import java.util.Map;

/**
 * @author anchore
 * @date 2018/6/19
 */
public class RealtimeRangeDeleter extends RangeDeleter {
    public RealtimeRangeDeleter(Segment segment, Map<String, Interval> intervals) {
        super(segment, intervals);
    }

    @Override
    protected void putAllShowIndex(MutableBitMap toBeDeleted) {
        super.putAllShowIndex(toBeDeleted);

        Segment backupSeg = getBackupSegment();
        backupSeg.putAllShowIndex(backupSeg.getAllShowIndex().getAndNot(toBeDeleted));
        releaseIfNeed(backupSeg);
    }

    private Segment getBackupSegment() {
        SwiftMetaData meta = segment.getMetaData();
        String segPath = segment.getLocation().getPath();
        return new HistorySegmentImpl(new ResourceLocation(segPath.replace(meta.getSwiftSchema().getDir(), Schema.BACKUP_CUBE.getDir()), StoreType.FINE_IO), meta);
    }
}