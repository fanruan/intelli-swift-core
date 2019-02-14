package com.fr.swift.segment.operator.insert;

import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.cube.io.Releasable;
import com.fr.swift.db.Database;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.event.SyncSegmentLocationEvent;
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
import com.fr.swift.util.Assert;
import com.fr.swift.util.IoUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

    @Override
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
                    insertings.put(segInfo, getInserting(seg));
                    importSegKeys.add(segKey);
                }
                insertings.get(segInfo).insert(row);
            }

            SwiftEventDispatcher.fire(SyncSegmentLocationEvent.PUSH_SEG, importSegKeys);
        } catch (Throwable e) {
            SwiftLoggers.getLogger().error(e);
        } finally {
            // todo 报错后如何处置，脏数据清掉？
            IoUtil.close(swiftResultSet);
            IoUtil.release(this);
        }
    }

    private SegmentInfo allot(int cursor, Row row) {
        if (alloter.getAllotRule().getType() == AllotType.HASH) {
            return alloter.allot(new HashRowInfo(row));
        }
        return alloter.allot(new LineRowInfo(cursor));
    }

    protected abstract Inserting getInserting(Segment seg);

    protected abstract Segment newSegment(SegmentKey segmentKey);

    protected abstract void handleFullSegment(SegmentInfo segInfo);

    protected SegmentKey newSegmentKey(SegmentInfo segInfo) {
        return new SegmentKeyBean(dataSource.getSourceKey(), segInfo.getOrder(), segInfo.getStoreType(), dataSource.getMetadata().getSwiftDatabase());
    }

    private void releaseFullIfExists() {
        for (Iterator<Entry<SegmentInfo, Inserting>> itr = insertings.entrySet().iterator(); itr.hasNext(); ) {
            Entry<SegmentInfo, Inserting> entry = itr.next();
            if (entry.getValue().isFull()) {
                IoUtil.release(entry.getValue());

                // 处理满了的块，比如上传历史块或者持久化增量块
                handleFullSegment(entry.getKey());

                itr.remove();
            }
        }
    }

    @Override
    public void release() {
        for (Iterator<Entry<SegmentInfo, Inserting>> itr = insertings.entrySet().iterator(); itr.hasNext(); ) {
            Entry<SegmentInfo, Inserting> entry = itr.next();
            IoUtil.release(entry.getValue());

            if (entry.getValue().isFull()) {
                // 处理满了的块，比如上传历史块或者持久化增量块
                handleFullSegment(entry.getKey());
            }
            itr.remove();
        }
    }

    protected class Inserting implements Releasable {
        private Inserter inserter;

        private Segment seg;

        private int rowCount;

        public Inserting(Inserter inserter, Segment seg, int rowCount) {
            Assert.isTrue(rowCount >= 0);

            this.inserter = inserter;
            this.seg = seg;
            this.rowCount = rowCount;
        }

        void insert(Row row) throws Exception {
            inserter.insertData(row);
            rowCount++;
        }

        boolean isFull() {
            return rowCount >= alloter.getAllotRule().getCapacity();
        }

        public Segment getSegment() {
            return seg;
        }

        @Override
        public void release() {
            IoUtil.release(inserter);
        }
    }

    @Override
    public List<String> getFields() {
        return dataSource.getMetadata().getFieldNames();
    }

    @Override
    public List<SegmentKey> getImportSegments() {
        return importSegKeys;
    }
}