package com.fr.swift.segment.operator.insert;

import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.cube.io.Releasable;
import com.fr.swift.db.Database;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.operator.Importer;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.RowInfo;
import com.fr.swift.source.alloter.SegmentInfo;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.source.alloter.impl.BaseAllotRule.AllotType;
import com.fr.swift.source.alloter.impl.hash.HashRowInfo;
import com.fr.swift.source.alloter.impl.line.LineRowInfo;
import com.fr.swift.util.IoUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author anchore
 * @date 2018/8/1
 */
public abstract class BaseBlockImporter<A extends SwiftSourceAlloter<?, RowInfo>> implements Releasable, Importer {

    private A alloter;

    protected DataSource dataSource;

    protected Map<SegmentInfo, Inserting> insertings = new HashMap<SegmentInfo, Inserting>();

    private List<SegmentKey> importSegKeys = new ArrayList<SegmentKey>();

    public BaseBlockImporter(DataSource dataSource, A alloter) {
        this.dataSource = dataSource;
        this.alloter = alloter;
    }

    private void persistMeta() throws SQLException {
        Database db = SwiftDatabase.getInstance();
        SourceKey tableKey = dataSource.getSourceKey();
        // todo 分布式导入可能有多线程坑
        if (!db.existsTable(tableKey)) {
            db.createTable(tableKey, dataSource.getMetadata());
        }
    }

    public void importData(SwiftResultSet swiftResultSet) throws Exception {
        try {
            persistMeta();

            int cursor = 0;
            while (swiftResultSet.hasNext()) {
                Row row = swiftResultSet.getNextRow();
                SegmentInfo segInfo = allot(cursor++, row);

                if (!insertings.containsKey(segInfo)) {
                    // 可能有满了的seg
                    // todo 如果增量走这边，要fire upload的
                    releaseFullIfExists();
                    SegmentKey segKey = newSegmentKey(segInfo);
                    Segment seg = newSegment(segKey);
                    insertings.put(segInfo, new Inserting(getInserter(seg), seg));
                    importSegKeys.add(segKey);
                }
                insertings.get(segInfo).insert(row);
            }
        } finally {
            // todo 报错后如何处置，脏数据清掉？
            IoUtil.close(swiftResultSet);
            IoUtil.release(this);
        }
    }

    private SegmentInfo allot(int cursor, Row row) {
        if (alloter.getAllotRule().getType() == AllotType.HASH) {
            return alloter.allot(new HashRowInfo(cursor, row));
        }
        return alloter.allot(new LineRowInfo(cursor));
    }

    protected abstract Inserter getInserter(Segment seg);

    protected SegmentKey newSegmentKey(SegmentInfo segInfo) {
        SegmentKey segKey = new SegmentKeyBean(dataSource.getSourceKey(), segInfo.getOrder(), segInfo.getStoreType(), dataSource.getMetadata().getSwiftDatabase());
        return segKey;
    }

    protected abstract Segment newSegment(SegmentKey segmentKey);

    protected abstract void releaseFullIfExists();

    @Override
    public void release() {
        for (Inserting inserting : insertings.values()) {
            IoUtil.release(inserting);
        }
    }

    protected class Inserting implements Releasable {
        private Inserter inserter;

        private Segment seg;

        public Inserting(Inserter inserter, Segment seg) {
            this.inserter = inserter;
            this.seg = seg;
        }

        void insert(Row row) throws Exception {
            inserter.insertData(row);
        }

        public boolean isFull() {
            if (!seg.isReadable()) {
                return false;
            }
            return seg.getRowCount() >= alloter.getAllotRule().getCapacity();
        }

        @Override
        public void release() {
            IoUtil.release(inserter);
        }
    }

    public List<String> getFields() {
        return dataSource.getMetadata().getFieldNames();
    }

    @Override
    public List<SegmentKey> getImportSegments() {
        return importSegKeys;
    }
}