package com.fr.swift.segment.operator.insert;

import com.fr.swift.config.entity.SwiftTableAllotRule;
import com.fr.swift.cube.CubePathBuilder;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.db.Database;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.result.MutableResultSet;
import com.fr.swift.segment.MutableCacheColumnSegment;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.event.SegmentEvent;
import com.fr.swift.segment.event.SyncSegmentLocationEvent;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.alloter.RowInfo;
import com.fr.swift.source.alloter.SegmentInfo;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.util.IoUtil;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

/**
 * @author lucifer
 * @date 2019/7/24
 * @description
 * @since swift 1.1
 * fixme 和HIstoryBlockImport重叠的部分再处理
 */
public class MutableImporter<A extends SwiftSourceAlloter<?, RowInfo>> extends BaseBlockImporter<A, MutableResultSet> {

    protected Integer curCursor = 0;

    public MutableImporter(DataSource dataSource, A alloter) {
        super(dataSource, alloter);
    }

    @Override
    public void importData(MutableResultSet mutableResultSet) throws Exception {
        try {
            persistMeta();
            for (int cursor = 0; mutableResultSet.hasNext(); cursor++) {
                insertRow(mutableResultSet, mutableResultSet.getNextRow(), cursor);
            }
            onSucceed();
        } finally {
            IoUtil.close(mutableResultSet);
            IoUtil.release(this);
        }
    }

    /**
     * 导入一行数据（保存对 importer 的引用可以连续按行导入）
     *
     * @param mutableResultSet
     * @return
     * @throws Exception
     */
    public void importRow(MutableResultSet mutableResultSet) throws Exception {
        if (mutableResultSet.hasNext()) {
            insertRow(mutableResultSet, mutableResultSet.getNextRow(), curCursor);
            curCursor++;
        }
    }

    private void insertRow(MutableResultSet mutableResultSet, Row row, int cursor) throws Exception {
        SegmentInfo segInfo = allot(cursor, row);

        if (mutableResultSet.hasNewSubfields()) {
            for (Map.Entry<SegmentInfo, Inserting> insertingEntry : insertings.entrySet()) {
                ((MutableInserting) insertingEntry.getValue()).refreshMetadata(mutableResultSet.getMetaData());
            }
            //todo Database增加一个接口
            SwiftDatabase.getInstance().dropTable(new SourceKey(mutableResultSet.getMetaData().getTableName()));
            SwiftDatabase.getInstance().createTable(new SourceKey(mutableResultSet.getMetaData().getTableName()), mutableResultSet.getMetaData());
        }

        if (!insertings.containsKey(segInfo)) {
            releaseFullIfExists();
            // 可能有满了的seg
            SegmentKey segKey = newSegmentKey(segInfo);
            insertings.put(segInfo, getInserting(segKey));
            if (mutableResultSet.hasNewSubfields()) {
                ((MutableInserting) insertings.get(segInfo)).refreshMetadata(mutableResultSet.getMetaData());
            }
            importSegKeys.add(segKey);
        }
        insertings.get(segInfo).insert(row);
    }

    /**
     * importRow 之前需要执行的初始化
     *
     * @param tableKey
     * @param metaData
     * @throws SQLException
     */
    public void initImportRow(SourceKey tableKey, SwiftMetaData metaData) throws SQLException {
        Database db = SwiftDatabase.getInstance();
        // todo 分布式导入可能有多线程坑
        if (!db.existsTable(tableKey)) {
            db.createTable(tableKey, metaData);
        }
        if (swiftTableAllotRuleService.getAllotRuleByTable(tableKey) == null) {
            SwiftTableAllotRule swiftTableAllotRule = new SwiftTableAllotRule(tableKey.getId(), alloter.getAllotRule().getType().name(), alloter.getAllotRule());
            swiftTableAllotRuleService.saveAllotRule(swiftTableAllotRule);
        }
    }

    public void finishImportRow() {
        onSucceed();
        IoUtil.release(this);
    }

    @Override
    protected MutableInserting getInserting(SegmentKey segKey) {
        // FIXME 2019/3/13 anchore 有临时路径的坑 tempDir应为当前导入的路径
        ResourceLocation location = new ResourceLocation(new CubePathBuilder(segKey).setTempDir(CubeUtil.getCurrentDir(segKey.getTable())).build(), segKey.getStoreType());
        Segment seg = new MutableCacheColumnSegment(location, dataSource.getMetadata());
        return new MutableInserting(MutableInserter.ofOverwriteMode(seg), seg, 0);
    }

    @Override
    protected void handleFullSegment(SegmentInfo segInfo) {
        try {
            SegmentKey segKey = newSegmentKey(segInfo);
            SwiftEventDispatcher.syncFire(SegmentEvent.UPLOAD_HISTORY, segKey);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    @Override
    protected void onSucceed() {
        segLocationSvc.saveOrUpdateLocal(new HashSet<>(importSegKeys));
        SwiftEventDispatcher.fire(SyncSegmentLocationEvent.PUSH_SEG, importSegKeys);
    }

    @Override
    protected void onFailed() {
        // do nothing
    }

    @Override
    protected void indexIfNeed(SegmentInfo segInfo) {
        try {
            // FIXME 2019/3/13 anchore 有临时路径的坑 tempDir应为当前导入的路径
            // todo 考虑异步，提升性能
            SegmentUtils.indexSegmentIfNeed(Collections.singletonList(insertings.get(segInfo).getSegment()));
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    protected class MutableInserting extends Inserting<MutableInserter> {

        public MutableInserting(MutableInserter inserter, Segment seg, int rowCount) {
            super(inserter, seg, rowCount);
        }

        void refreshMetadata(SwiftMetaData metaData) {
            inserter.refreshMetadata(metaData);
        }
    }
}