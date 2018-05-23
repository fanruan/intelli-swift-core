package com.fr.swift.segment.recover;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftDataOperatorProvider;
import com.fr.swift.segment.SwiftSegmentKey;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author anchore
 * @date 2018/5/23
 */
public class SwiftSegmentRecovery implements SegmentRecovery {
    private SwiftDataOperatorProvider operators = null;

    @Override
    public void recover(SourceKey tableKey) {
        DataSource table = null;
        List<SwiftSegmentKey> segKeys = getUnstoredSegments(tableKey);
        for (SwiftSegmentKey segKey : segKeys) {
            try {
                Inserter insert = operators.getRealtimeSwiftInserter(newRealtimeSegment(segKey), table);
                insert.insertData(new BackupResultSet(getBackupSegment(segKey)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Segment getBackupSegment(SegmentKey segKey) {
        return null;
    }


    private Segment newRealtimeSegment(SegmentKey segKey) {
        return null;
    }

    private List<SwiftSegmentKey> getUnstoredSegments(SourceKey tableKey) {
        return null;
    }

    @Override
    public void recoverAll() {
        List<SourceKey> tables = new ArrayList<SourceKey>();
        for (SourceKey table : tables) {
            recover(table);
        }
    }

    static class BackupResultSet implements SwiftResultSet {
        SwiftMetaData meta;
        List<DetailColumn> details = new ArrayList<DetailColumn>();
        int cursor;
        int rowCount;

        BackupResultSet(Segment seg) throws SQLException {
            init(seg);
        }

        void init(Segment seg) throws SQLException {
            meta = seg.getMetaData();
            rowCount = seg.getRowCount();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                details.add(seg.getColumn(new ColumnKey(meta.getColumnName(i))).getDetailColumn());
            }
        }

        @Override
        public SwiftMetaData getMetaData() {
            return meta;
        }

        @Override
        public boolean next() {
            return cursor++ < rowCount;
        }

        @Override
        public Row getRowData() {
            List<Object> row = new ArrayList<Object>();
            for (DetailColumn detail : details) {
                row.add(detail.get(cursor));
            }
            return new ListBasedRow(row);
        }

        @Override
        public void close() {
            for (DetailColumn detail : details) {
                detail.release();
            }
        }
    }

}