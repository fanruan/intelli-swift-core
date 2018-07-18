package com.fr.swift.segment.operator.delete;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.db.Where;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.segment.HistorySegmentImpl;
import com.fr.swift.segment.Segment;
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
public class RealtimeSwiftDeleter extends AbstractDeleter {

    public AllShowIndexBackup allShowIndexBackup;

    public RealtimeSwiftDeleter(Segment segment) {
        super(segment);
        allShowIndexBackup = (AllShowIndexBackup) SwiftContext.get().getBean("allShowIndexBackup", getBackupSegment());
    }

    @Override
    public ImmutableBitMap delete(SourceKey sourceKey, Where where) throws Exception {
        ImmutableBitMap allshowIndex = super.delete(sourceKey, where);
        allShowIndexBackup.backupAllShowIndex(allshowIndex);
        return allshowIndex;
    }

    @Override
    public void release() {
    }

    private Segment getBackupSegment() {
        SwiftMetaData meta = segment.getMetaData();
        String segPath = segment.getLocation().getPath();
        return new HistorySegmentImpl(new ResourceLocation(segPath.replace(meta.getSwiftSchema().getDir(), SwiftDatabase.Schema.BACKUP_CUBE.getDir()), Types.StoreType.FINE_IO), meta);
    }
}
