package com.fr.swift.segment.operator.insert;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.config.service.SwiftSegmentLocationService;
import com.fr.swift.cube.io.Releasable;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentService;
import com.fr.swift.segment.operator.Importer;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.Row;
import com.fr.swift.source.alloter.RowInfo;
import com.fr.swift.source.alloter.SegmentInfo;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.source.alloter.impl.BaseAllotRule.AllotType;
import com.fr.swift.source.alloter.impl.hash.HashRowInfo;
import com.fr.swift.source.alloter.impl.line.LineRowInfo;
import com.fr.swift.util.Assert;
import com.fr.swift.util.IoUtil;

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
public abstract class BaseBlockImporter<A extends SwiftSourceAlloter<?, RowInfo>, R extends SwiftResultSet> implements Releasable, Importer<R> {

    protected int curCursor = 0;

    protected A alloter;

    protected DataSource dataSource;

    protected Map<SegmentInfo, Inserting> insertings = new HashMap<>();

    protected List<SegmentKey> importSegKeys = new ArrayList<>();

    protected SwiftSegmentLocationService segLocationSvc = SwiftContext.get().getBean(SwiftSegmentLocationService.class);

    protected SegmentService segmentService = SwiftContext.get().getBean(SegmentService.class);

    public BaseBlockImporter(DataSource dataSource, A alloter) {
        this.dataSource = dataSource;
        this.alloter = alloter;
    }

    @Override
    public void importResultSet(R swiftResultSet) throws Exception {
        try (R resultSet = swiftResultSet) {
            for (int cursor = 0; resultSet.hasNext(); cursor++) {
                importRow(resultSet.getNextRow(), cursor);
            }
            IoUtil.release(this);
            processAfterSegmentDone(true);
            onSucceed();
        } catch (Throwable e) {
            SwiftLoggers.getLogger().error(e);
            IoUtil.release(this);
            processAfterSegmentDone(false);
            onFailed();
            throw e;
        }
    }

    protected void importRow(Row row) throws Exception {
        importRow(row, curCursor);
        curCursor++;
    }

    protected void importRow(Row row, int cursor) throws Exception {
        SegmentInfo segInfo = allot(cursor, row);
        if (!insertings.containsKey(segInfo)) {
            releaseFullIfExists();
            indexFullIfExists();
            // 可能有满了的seg
            SegmentKey segKey = newSegmentKey(segInfo);
            insertings.put(segInfo, getInserting(segKey));
            importSegKeys.add(segKey);
        }
        insertings.get(segInfo).insert(row);
    }

    protected SegmentInfo allot(int cursor, Row row) {
        if (alloter.getAllotRule().getType() == AllotType.HASH) {
            return alloter.allot(new HashRowInfo(row));
        }
        return alloter.allot(new LineRowInfo(cursor));
    }

    protected abstract Inserting getInserting(SegmentKey segKey);

    /**
     * 处理满了的块，比如上传历史块或者持久化增量块
     *
     * @param segInfo seg info
     */
    protected abstract void handleFullSegment(SegmentInfo segInfo);

    protected abstract void onSucceed();

    protected abstract void onFailed();

    protected SegmentKey newSegmentKey(SegmentInfo segInfo) {
        return new SwiftSegmentEntity(dataSource.getSourceKey(), segInfo.getOrder(), segInfo.getStoreType(), dataSource.getMetadata().getSwiftDatabase(), segInfo.getYearMonth());
    }

    protected void releaseFullIfExists() {
        for (Entry<SegmentInfo, Inserting> entry : insertings.entrySet()) {
            if (entry.getValue().isFull()) {
                IoUtil.release(entry.getValue());
            }
        }
    }

    protected void indexFullIfExists() throws Exception {
        for (Iterator<Entry<SegmentInfo, Inserting>> itr = insertings.entrySet().iterator(); itr.hasNext(); ) {
            Entry<SegmentInfo, Inserting> entry = itr.next();
            if (entry.getValue().isFull()) {
                indexIfNeed(entry.getKey());

                // 处理满了的块，比如上传历史块或者持久化增量块
                handleFullSegment(entry.getKey());

                itr.remove();
            }
        }
    }

    @Override
    public void release() {
        for (Entry<SegmentInfo, Inserting> entry : insertings.entrySet()) {
            IoUtil.release(entry.getValue());
        }
    }

    protected void indexIfNeed(SegmentInfo segInfo) throws Exception {
    }

    protected void processAfterSegmentDone(boolean needIndex) throws Exception {
        for (Iterator<Entry<SegmentInfo, Inserting>> itr = insertings.entrySet().iterator(); itr.hasNext(); ) {
            Entry<SegmentInfo, Inserting> entry = itr.next();
            if (needIndex) {
                indexIfNeed(entry.getKey());
                // TODO: 2019/10/8 未满历史块不会走
                if (entry.getValue().isFull()) {
                    handleFullSegment(entry.getKey());
                }
            }
            itr.remove();
        }
    }

    public class Inserting<I extends Inserter> implements Releasable {
        protected I inserter;

        private Segment seg;

        private int rowCount;

        public Inserting(I inserter, Segment seg, int rowCount) {
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