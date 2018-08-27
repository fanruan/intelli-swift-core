package com.fr.swift.segment.operator.delete;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.db.Where;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.backup.AllShowIndexBackup;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;

/**
 * This class created on 2018/3/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class RealtimeSwiftDeleter extends SwiftWhereDeleter {
    private AllShowIndexBackup allShowIndexBackup;

    public RealtimeSwiftDeleter(SourceKey tableKey, Segment segment) {
        super(tableKey, segment);
        allShowIndexBackup = (AllShowIndexBackup) SwiftContext.get().getBean("allShowIndexBackup", getBackupSegment());
    }

    @Override
    public ImmutableBitMap delete(Where where) throws Exception {
        ImmutableBitMap allShowIndex = super.delete(where);
        allShowIndexBackup.backupAllShowIndex(allShowIndex);
        return allShowIndex;
    }

    private Segment getBackupSegment() {
        SwiftMetaData meta = segment.getMetaData();
        String segPath = segment.getLocation().getPath();
        SwiftDatabase swiftSchema = meta.getSwiftSchema();
        return SegmentUtils.newHistorySegment(new ResourceLocation(segPath.replace(swiftSchema.getDir(), swiftSchema.getBackupDir()), Types.StoreType.FINE_IO), meta);
    }
}
