package com.fr.swift.segment.recover;

import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.db.Table;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.HistorySegmentImpl;
import com.fr.swift.segment.RealTimeSegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftDataOperatorProvider;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author anchore
 * @date 2018/5/23
 */
@Service
public class SwiftSegmentRecovery implements SegmentRecovery {
    @Autowired
    private SwiftDataOperatorProvider operators;

    @Autowired
    private SwiftSegmentManager manager;

    @Override
    public void recover(SourceKey tableKey) {
        List<Segment> segs = getUnstoredSegments(tableKey);
        for (Segment seg : segs) {
            try {
                Table table = SwiftDatabase.getInstance().getTable(tableKey);
                Inserter insert = operators.getRealtimeSwiftInserter(newRealtimeSegment(seg), table);
                List<Segment> newSegs = insert.insertData(new BackupResultSet(getBackupSegment(seg)));
                for (String columnName : insert.getFields()) {
                    ColumnKey columnKey = new ColumnKey(columnName);
                    operators.getColumnIndexer(table, columnKey, newSegs).buildIndex();
                    operators.getColumnDictMerger(table, columnKey, newSegs).mergeDict();
                }
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
            }
        }
    }

    private Segment getBackupSegment(Segment realtimeSeg) {
        return new HistorySegmentImpl(new ResourceLocation(realtimeSeg.getLocation().getPath(), StoreType.FINE_IO), realtimeSeg.getMetaData());
    }

    private Segment newRealtimeSegment(Segment realtimeSeg) {
        return new RealTimeSegmentImpl(realtimeSeg.getLocation(), realtimeSeg.getMetaData());
    }

    private List<Segment> getUnstoredSegments(SourceKey tableKey) {
        List<Segment> segs = manager.getSegment(tableKey),
                unstoredSegs = new ArrayList<Segment>();
        for (Segment seg : segs) {
            if (seg.getLocation().getStoreType() == StoreType.MEMORY) {
                unstoredSegs.add(seg);
            }
        }
        return unstoredSegs;
    }

    @Override
    public void recoverAll() {
        List<Table> tables;
        try {
            tables = SwiftDatabase.getInstance().getAllTables();
            for (Table table : tables) {
                recover(table.getSourceKey());
            }
        } catch (SQLException e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    static class BackupResultSet implements SwiftResultSet {
        SwiftMetaData meta;
        List<DetailColumn> details = new ArrayList<DetailColumn>();
        int cursor = -1;
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
            return ++cursor < rowCount;
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

    private SwiftSegmentRecovery() {
    }
}