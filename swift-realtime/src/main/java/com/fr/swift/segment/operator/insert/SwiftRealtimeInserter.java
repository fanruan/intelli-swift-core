package com.fr.swift.segment.operator.insert;

import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.db.impl.SwiftDatabase.Schema;
import com.fr.swift.segment.HistorySegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.util.List;

/**
 * This class created on 2018/3/26
 *
 * @author Lucifer
 * @description 指定数据和块insert
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftRealtimeInserter extends SwiftInserter {
    private BackupInserter backupInserter;

    public SwiftRealtimeInserter(Segment segment) {
        this(segment, segment.getMetaData().getFieldNames());
    }

    public SwiftRealtimeInserter(Segment segment, List<String> fields) {
        super(segment, fields);
        backupInserter = new BackupInserter(getBackupSegment(), fields);
    }

    private Segment getBackupSegment() {
        SwiftMetaData meta = segment.getMetaData();
        String segPath = segment.getLocation().getPath();
        return new HistorySegmentImpl(new ResourceLocation(segPath.replace(meta.getSwiftSchema().getDir(), Schema.BACKUP_CUBE.getDir()), StoreType.FINE_IO), meta);
    }

    @Override
    protected void putRow(int cursor, Row rowData) {
        super.putRow(cursor, rowData);
        backupInserter.putRow(cursor, rowData);
    }

    @Override
    protected void putNullIndex() {
        super.putNullIndex();
        backupInserter.putNullIndex();
    }

    @Override
    protected void putSegmentInfo(int lastCursor, int cursor) {
        super.putSegmentInfo(lastCursor, cursor);
        backupInserter.putSegmentInfo(lastCursor, cursor);
    }

    @Override
    protected void release() {
        super.release();
        backupInserter.release();
    }

    private static class BackupInserter extends BaseInserter {
        BackupInserter(Segment segment, List<String> fields) {
            super(segment, fields);
        }
    }
}