package com.fr.swift.segment.recover;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.db.Schema;
import com.fr.swift.db.Table;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.segment.RealTimeSegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;
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
        for (Table table : SwiftDatabase.getInstance().getAllTables()) {
            recover(table.getSourceKey());
        }
    }

    protected Segment getBackupSegment(Segment realtimeSeg) {
        SwiftMetaData meta = realtimeSeg.getMetaData();
        String realtimeSegPath = realtimeSeg.getLocation().getPath();
        Schema swiftSchema = meta.getSwiftSchema();
        return SegmentUtils.newHistorySegment(new ResourceLocation(realtimeSegPath.replace(swiftSchema.getDir(), swiftSchema.getBackupDir()), Types.StoreType.NIO), meta);
    }

    protected Segment newRealtimeSegment(Segment realtimeSeg) {
        return new RealTimeSegmentImpl(realtimeSeg.getLocation(), realtimeSeg.getMetaData());
    }

    private List<SegmentKey> getUnstoredSegmentKeys(SourceKey tableKey) {
        List<SegmentKey> segKeys = localSegmentProvider.getSegmentKeys(tableKey);
        List<SegmentKey> unstoredSegs = new ArrayList<SegmentKey>();
        if (null != segKeys) {
            for (SegmentKey segKey : segKeys) {
                if (segKey.getStoreType() == Types.StoreType.MEMORY) {
                    unstoredSegs.add(segKey);
                }
            }
        }
        return unstoredSegs;
    }
}
