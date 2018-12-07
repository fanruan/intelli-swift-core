package com.fr.swift.segment.operator.delete;

import com.fr.swift.SwiftContext;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.cube.CubePathBuilder;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.db.Table;
import com.fr.swift.db.Where;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.segment.MutableHistorySegment;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.backup.AllShowIndexBackup;

/**
 * This class created on 2018/3/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class RealtimeSwiftDeleter implements WhereDeleter {

    private static final SwiftSegmentManager LOCAL_SEGS = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);

    private SegmentKey segKey;

    private AllShowIndexBackup allShowIndexBackup;

    public RealtimeSwiftDeleter(SegmentKey segKey) {
        this.segKey = segKey;
        allShowIndexBackup = (AllShowIndexBackup) SwiftContext.get().getBean("allShowIndexBackup", getBackupSegment());
    }

    @Override
    public ImmutableBitMap delete(Where where) throws Exception {
        Table table = SwiftDatabase.getInstance().getTable(segKey.getTable());
        Segment seg = LOCAL_SEGS.getSegment(segKey);

        ImmutableBitMap originAllShowIndex = seg.getAllShowIndex();
        ImmutableBitMap indexAfterFilter = where.createWhereIndex(table, seg);
        ImmutableBitMap allShowIndex = originAllShowIndex.getAndNot(indexAfterFilter);
        seg.putAllShowIndex(allShowIndex);
        allShowIndexBackup.backupAllShowIndex(allShowIndex);
        return allShowIndex;
    }

    private Segment getBackupSegment() {
        String backupPath = new CubePathBuilder(segKey).asBackup().build();
        return new MutableHistorySegment(
                new ResourceLocation(backupPath, Types.StoreType.NIO),
                SwiftDatabase.getInstance().getTable(segKey.getTable()).getMetadata());
    }
}
