package com.fr.swift.segment.recover;

import com.fr.swift.SwiftContext;
import com.fr.swift.cube.CubePathBuilder;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.db.Table;
import com.fr.swift.segment.HistorySegmentImpl;
import com.fr.swift.segment.RealtimeSegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;

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
    protected SwiftSegmentManager localSegmentProvider = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);

    @Override
    public void recover(SourceKey tableKey) {
        recover(getUnstoredSegmentKeys(tableKey));
    }

    @Override
    public void recoverAll() {
        for (Table table : com.fr.swift.db.impl.SwiftDatabase.getInstance().getAllTables()) {
            recover(table.getSourceKey());
        }
    }

    protected Segment getBackupSegment(SegmentKey realtimeSegKey, SwiftMetaData meta) {
        // 恢复用的，只能读不能写
        return new HistorySegmentImpl(new ResourceLocation(new CubePathBuilder(realtimeSegKey).asBackup().build(), Types.StoreType.NIO), meta);
    }

    protected Segment newRealtimeSegment(Segment realtimeSeg) {
        return new RealtimeSegmentImpl(realtimeSeg.getLocation(), realtimeSeg.getMetaData());
    }

    private List<SegmentKey> getUnstoredSegmentKeys(SourceKey tableKey) {
        List<SegmentKey> segKeys = localSegmentProvider.getSegmentKeys(tableKey);
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
