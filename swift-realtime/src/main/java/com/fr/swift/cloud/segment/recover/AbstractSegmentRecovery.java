package com.fr.swift.cloud.segment.recover;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.cube.CubePathBuilder;
import com.fr.swift.cloud.cube.io.Types;
import com.fr.swift.cloud.cube.io.location.ResourceLocation;
import com.fr.swift.cloud.db.Table;
import com.fr.swift.cloud.db.impl.SwiftDatabase;
import com.fr.swift.cloud.segment.BackupSegment;
import com.fr.swift.cloud.segment.RealtimeSegmentImpl;
import com.fr.swift.cloud.segment.Segment;
import com.fr.swift.cloud.segment.SegmentKey;
import com.fr.swift.cloud.segment.SegmentService;
import com.fr.swift.cloud.source.SourceKey;
import com.fr.swift.cloud.source.SwiftMetaData;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018/6/22
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public abstract class AbstractSegmentRecovery implements SegmentRecovery {
    protected SegmentService segmentService = SwiftContext.get().getBean(SegmentService.class);

    @Override
    public void recover(SourceKey tableKey) {
        recover(getUnstoredSegmentKeys(tableKey));
    }

    @Override
    public void recoverAll() {
        for (Table table : SwiftDatabase.getInstance().getAllTables()) {
            recover(table.getSourceKey());
        }
    }

    protected Segment getBackupSegment(SegmentKey realtimeSegKey, SwiftMetaData meta) {
        // 恢复用的，只能读不能写
        return new BackupSegment(new ResourceLocation(new CubePathBuilder(realtimeSegKey).asBackup().build(), Types.StoreType.NIO), meta);
    }

    protected Segment newRealtimeSegment(Segment realtimeSeg) {
        return new RealtimeSegmentImpl(realtimeSeg.getLocation(), realtimeSeg.getMetaData());
    }

    private List<SegmentKey> getUnstoredSegmentKeys(SourceKey tableKey) {
        List<SegmentKey> segKeys = segmentService.getSegmentKeys(tableKey);
        List<SegmentKey> unstoredSegs = new ArrayList<SegmentKey>();
        if (null != segKeys) {
            for (SegmentKey segKey : segKeys) {
                if (segKey.getStoreType().isTransient()) {
                    unstoredSegs.add(segKey);
                }
            }
        }
        return unstoredSegs;
    }
}
