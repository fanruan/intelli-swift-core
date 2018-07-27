package com.fr.swift.service;

import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.service.SwiftSegmentServiceProvider;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.db.Table;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentResultSet;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.operator.insert.SwiftInserter;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author anchore
 * @date 2018/7/27
 */
public class HistorySegmentPutter implements Runnable {
    private static final int MIN_PUT_THRESHOLD = 10000;

    private final SwiftSegmentManager localSegments = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);

    public HistorySegmentPutter() {
        SwiftExecutors.newScheduledThreadPool(1, new PoolThreadFactory(getClass())).
                scheduleWithFixedDelay(new HistorySegmentPutter(), 0, 1, TimeUnit.HOURS);
    }

    @Override
    public void run() {
        for (Table table : SwiftDatabase.getInstance().getAllTables()) {
            for (SegmentKey segKey : localSegments.getSegmentKeys(table.getSourceKey())) {
                if (segKey.getStoreType() != StoreType.MEMORY) {
                    continue;
                }
                Segment realtimeSeg = localSegments.getSegment(segKey);
                if (realtimeSeg.isReadable() && realtimeSeg.getRowCount() > MIN_PUT_THRESHOLD) {
                    putHistorySegment(segKey, realtimeSeg);
                }
            }
        }
    }

    public static void putHistorySegment(SegmentKey realtimeSegKey, Segment realtimeSeg) {
        List<SegmentKey> newHisSeg = Collections.<SegmentKey>singletonList(
                new SegmentKeyBean(realtimeSegKey.getTable().getId(), realtimeSegKey.getUri(), realtimeSegKey.getOrder(), StoreType.FINE_IO, realtimeSegKey.getSwiftSchema()));

        try {
            SwiftSegmentServiceProvider.getProvider().addSegments(newHisSeg);
            Segment hisSeg = SegmentUtils.newHistorySegment(new ResourceLocation(realtimeSeg.getLocation().getPath(), StoreType.FINE_IO), realtimeSeg.getMetaData());
            new SwiftInserter(hisSeg).insertData(new SegmentResultSet(realtimeSeg));

            triggerCollate(realtimeSegKey.getTable());
        } catch (Exception e) {
            SwiftLoggers.getLogger().warn("{} put into history failed", realtimeSegKey);
            SwiftSegmentServiceProvider.getProvider().removeSegments(newHisSeg);
//            FileUtil.delete(realtimeSegKey.getAbsoluteUri().getPath());
        }
    }

    private static void triggerCollate(SourceKey tableKey) {
        try {
            SwiftContext.get().getBean(CollateService.class).autoCollateHistory(tableKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}