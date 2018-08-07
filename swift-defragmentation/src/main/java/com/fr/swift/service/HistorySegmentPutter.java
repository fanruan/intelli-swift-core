package com.fr.swift.service;

import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.cube.io.ResourceDiscovery;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.db.Table;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.db.impl.SwiftDatabase.Schema;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentResultSet;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.operator.column.SwiftColumnDictMerger;
import com.fr.swift.segment.operator.column.SwiftColumnIndexer;
import com.fr.swift.segment.operator.insert.SwiftInserter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import com.fr.swift.util.FileUtil;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;
import com.fr.swift.util.function.Predicate;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author anchore
 * @date 2018/7/27
 */
public class HistorySegmentPutter implements Runnable {
    private static final int MIN_PUT_THRESHOLD = LineAllotRule.MEM_STEP / 2;

    private final SwiftSegmentManager localSegments = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);

    public HistorySegmentPutter() {
        SwiftExecutors.newScheduledThreadPool(1, new PoolThreadFactory(getClass())).
                scheduleWithFixedDelay(this, 0, 1, TimeUnit.HOURS);
    }

    @Override
    public void run() {
        for (Table table : SwiftDatabase.getInstance().getAllTables()) {
            for (SegmentKey segKey : localSegments.getSegmentKeys(table.getSourceKey())) {
                if (segKey.getStoreType() != StoreType.MEMORY) {
                    continue;
                }
                Segment realtimeSeg = localSegments.getSegment(segKey);
                if (realtimeSeg.isReadable() && realtimeSeg.getAllShowIndex().getCardinality() > MIN_PUT_THRESHOLD) {
                    putHistorySegment(segKey, realtimeSeg);
                }
            }
        }
    }

    public static void putHistorySegment(final SegmentKey realtimeSegKey, Segment realtimeSeg) {
        SourceKey tableKey = realtimeSegKey.getTable();
        Schema swiftSchema = realtimeSegKey.getSwiftSchema();
        List<SegmentKey> newHisSeg = Collections.<SegmentKey>singletonList(
                new SegmentKeyBean(tableKey.getId(), realtimeSegKey.getUri(), realtimeSegKey.getOrder(), StoreType.FINE_IO, swiftSchema));

        try {
            // 先占坑
            SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class).addSegments(newHisSeg);
            Segment hisSeg = newHistory(realtimeSegKey);
            new SwiftInserter(hisSeg).insertData(new SegmentResultSet(realtimeSeg));

            SwiftMetaData metadata = realtimeSeg.getMetaData();
            // todo 暂时同步做索引
            for (int i = 0; i < metadata.getColumnCount(); i++) {
                ColumnKey columnKey = new ColumnKey(metadata.getColumnName(i + 1));
                List<Segment> segs = Collections.singletonList(hisSeg);
                Table table = SwiftDatabase.getInstance().getTable(tableKey);
                ((SwiftColumnIndexer) SwiftContext.get().getBean("columnIndexer", table, columnKey, segs)).buildIndex();
                ((SwiftColumnDictMerger) SwiftContext.get().getBean("columnDictMerger", table, columnKey, segs)).mergeDict();
            }

            // 写成功，清理realtime和备份
            SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class).removeSegments(Collections.singletonList(realtimeSegKey));
            ResourceDiscovery.getInstance().removeIf(new Predicate<String>() {
                @Override
                public boolean test(String s) {
                    return s.contains(realtimeSegKey.getUri().getPath());
                }
            });
            FileUtil.delete(realtimeSegKey.getAbsoluteUri().getPath().replace(swiftSchema.getDir(), swiftSchema.getBackupDir()));

            triggerCollate(tableKey);
        } catch (Exception e) {
            SwiftLoggers.getLogger().warn("{} put into history failed", realtimeSegKey);
            // 失败则清理无用seg
            SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class).removeSegments(newHisSeg);
            FileUtil.delete(realtimeSegKey.getAbsoluteUri().getPath());
        }
    }

    private static Segment newHistory(SegmentKey realtimeSegKey) {
        DataSource ds = SwiftDatabase.getInstance().getTable(realtimeSegKey.getTable());
        return SegmentUtils.newHistorySegment(
                new ResourceLocation(CubeUtil.getHistorySegPath(ds, realtimeSegKey.getOrder())), ds.getMetadata());
    }

    private static void triggerCollate(SourceKey tableKey) {
        try {
            SwiftContext.get().getBean(CollateService.class).autoCollateHistory(tableKey);
        } catch (Exception ignore) {
        }
    }
}