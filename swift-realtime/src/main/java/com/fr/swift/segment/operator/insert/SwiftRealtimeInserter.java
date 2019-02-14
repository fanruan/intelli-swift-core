package com.fr.swift.segment.operator.insert;

import com.fr.swift.SwiftContext;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.MutableHistorySegment;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.backup.SwiftSegmentBackup;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.transaction.Transactional;
import com.fr.swift.util.IoUtil;

import java.sql.SQLException;
import java.util.List;

/**
 * This class created on 2018/3/26
 *
 * @author Lucifer
 * @description 指定数据和块insert
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftRealtimeInserter extends SwiftInserter {

    private SwiftSegmentBackup swiftBackup;

    public SwiftRealtimeInserter(Segment segment) {
        this(segment, segment.getMetaData().getFieldNames());
    }

    public SwiftRealtimeInserter(Segment segment, List<String> fields) {
        super(segment, fields);
        swiftBackup = (SwiftSegmentBackup) SwiftContext.get().getBean("segmentBackup", getBackupSegment(), segment, fields);
    }

    private Segment getBackupSegment() {
        SwiftMetaData meta = segment.getMetaData();
        String segPath = segment.getLocation().getPath();
        SwiftDatabase swiftSchema = meta.getSwiftDatabase();
        return new MutableHistorySegment(new ResourceLocation(segPath.replace(swiftSchema.getDir(), swiftSchema.getBackupDir()), StoreType.NIO), meta);
    }

    @Override
    protected void putRow(int cursor, Row rowData) {
        super.putRow(cursor, rowData);
        // 增量考虑到要可读，每行都写rowCount，allshow
        super.putSegmentInfo(lastCursor, cursor + 1);
        swiftBackup.backupRowData(cursor, rowData);
    }

    @Override
    protected void putNullIndex() {
        super.putNullIndex();
        swiftBackup.backupNullIndex();
    }

    @Override
    protected void putSegmentInfo(int lastCursor, int cursor) {
        super.putSegmentInfo(lastCursor, cursor);
        swiftBackup.backupSegmentInfo(lastCursor, cursor);
    }

    @Override
    @Transactional(value = RealtimeInsertException.class)
    public void insertData(SwiftResultSet swiftResultSet) throws RealtimeInsertException, SQLException {
        // fixme 事务要好好考虑，因为现在都是一行行插入，看事务控制到何种粒度
        try {
            super.insertData(swiftResultSet);
        } catch (Exception e) {
            throw new RealtimeInsertException(e);
        }
    }

    @Override
    void initCursors() {
        boolean readable = CubeUtil.isReadable(segment);
        cursor = lastCursor = readable ? segment.getRowCount() : 0;
    }

    @Override
    public void release() {
        IoUtil.release(swiftBackup);
        super.release();
    }

    public SwiftSegmentBackup getSwiftBackup() {
        return swiftBackup;
    }
}