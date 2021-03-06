package com.fr.swift.cloud.segment;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.config.service.SwiftMetaDataService;
import com.fr.swift.cloud.cube.CubePathBuilder;
import com.fr.swift.cloud.cube.io.Types;
import com.fr.swift.cloud.cube.io.location.IResourceLocation;
import com.fr.swift.cloud.cube.io.location.ResourceLocation;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.segment.column.ColumnKey;
import com.fr.swift.cloud.segment.column.impl.base.ResourceDiscovery;
import com.fr.swift.cloud.segment.operator.column.SwiftColumnIndexer;
import com.fr.swift.cloud.source.SourceKey;
import com.fr.swift.cloud.source.SwiftMetaData;
import com.fr.swift.cloud.util.Assert;
import com.fr.swift.cloud.util.FileUtil;
import com.fr.swift.cloud.util.IoUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class created on 2018/7/10
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SegmentUtils {

    public static Segment newBackupSegment(SegmentKey segKey) {
        ResourceLocation location = new ResourceLocation(new CubePathBuilder(segKey).asBackup().build(), Types.StoreType.NIO);
        SwiftMetaData metaData = SwiftContext.get().getBean(SwiftMetaDataService.class).getMeta(segKey.getTable());
        return SwiftContext.get().getBean("backupSegment", Segment.class, location, metaData);
    }

    // TODO: 2020/11/3 需要在此获得yearmouth路径
    public static Segment newSegment(SegmentKey segKey) {
        return newSegment(segKey, segKey.getSegmentUri());
    }

    public static Segment newSegment(SegmentKey segmentKey, String tmpPath) {
        Assert.notNull(segmentKey);

        String cubePath;
        if (segmentKey.getStoreType().isTransient()) {
            cubePath = new CubePathBuilder(segmentKey).build();
        } else {
            cubePath = new CubePathBuilder(segmentKey).setTempDir(tmpPath).build();
        }

        Types.StoreType storeType = segmentKey.getStoreType();
        ResourceLocation location = new ResourceLocation(cubePath, storeType);
        SourceKey sourceKey = segmentKey.getTable();
        SwiftMetaData metaData = SwiftContext.get().getBean(SwiftMetaDataService.class).getMeta(sourceKey);

        Assert.notNull(metaData);
        return SegmentUtils.newSegment(location, metaData);
    }

    public static Segment newSegment(IResourceLocation location, SwiftMetaData meta) {
        Assert.notNull(location);
        Assert.notNull(meta);

        if (location.getStoreType().isTransient()) {
            return SwiftContext.get().getBean("realtimeSegment", Segment.class, location, meta);
        }
        return SwiftContext.get().getBean("historySegment", Segment.class, location, meta);
    }

    public static boolean existSegment(SegmentKey segKey) {
        return FileUtil.exists(new CubePathBuilder(segKey).setTempDir(segKey.getSegmentUri()).asAbsolute().build());
    }

    /**
     * 只清数据，不清配置
     *
     * @param segKey seg key
     */
    public static void clearSegment(SegmentKey segKey) {
        if (segKey.getStoreType().isTransient()) {
            clearRealtimeSegment(segKey);
        } else {
            clearHistorySegment(segKey);
        }
    }

    /**
     * 只清数据，不清配置
     *
     * @param segKeys
     */
    public static void clearSegments(Collection<SegmentKey> segKeys) {
        for (SegmentKey segKey : segKeys) {
            clearSegment(segKey);
        }
    }

    private static void clearRealtimeSegment(SegmentKey segKey) {
        ResourceDiscovery.getInstance().releaseSegment(segKey.getSwiftSchema(), segKey.getTable(), segKey.getOrder());

        FileUtil.delete(new CubePathBuilder(segKey).asAbsolute().asBackup().build());
    }

    private static void clearHistorySegment(SegmentKey segKey) {
        FileUtil.delete(new CubePathBuilder(segKey).asAbsolute().setTempDir(segKey.getSegmentUri()).build());
    }

    public static void indexSegmentIfNeed(List<Segment> segs) throws Exception {
        final int MAX_RETRY_TIME = 3;

        if (segs == null || segs.isEmpty()) {
            return;
        }

        List<Segment> hisSegs = new ArrayList<Segment>();
        for (Segment seg : segs) {
            if (seg != null && seg.isHistory()) {
                hisSegs.add(seg);
            }
        }

        if (hisSegs.isEmpty()) {
            return;
        }

        SwiftMetaData metadata = hisSegs.get(0).getMetaData();
        // todo 或可多线程加速？
        for (int i = 0; i < metadata.getColumnCount(); i++) {
            ColumnKey columnKey = new ColumnKey(metadata.getColumnName(i + 1));
            int retryTime = 0;
            do {
                try {
                    SwiftContext.get().getBean("columnIndexer", SwiftColumnIndexer.class, columnKey, hisSegs).buildIndex();
                    break;
                } catch (Exception e) {
                    if (retryTime++ >= MAX_RETRY_TIME) {
                        throw e;
                    }
                }
            } while (true);
        }
    }

    /**
     * 释放历史块内部所有资源
     *
     * @param seg 块
     */
    public static void releaseHisSeg(Segment seg) {
        if (seg != null && seg.isHistory()) {
            IoUtil.release(seg);
        }
    }

    /**
     * 释放历史块内部用到的所有资源
     *
     * @param segs
     */
    public static void releaseHisSeg(Collection<? extends Segment> segs) {
        if (segs == null) {
            return;
        }
        for (Segment seg : segs) {
            releaseHisSeg(seg);
        }
    }

    /**
     * 释放历史块column
     *
     * @param column 列
     */
    public static void releaseHisColumn(Column<?> column) {
        if (column != null && column.getLocation().getStoreType().isPersistent()) {
            IoUtil.release(column.getDetailColumn(), column.getDictionaryEncodedColumn(), column.getBitmapIndex());
        }
    }

    public static void releaseHisColumn(Collection<? extends Column<?>> columns) {
        if (columns == null) {
            return;
        }
        for (Column<?> column : columns) {
            releaseHisColumn(column);
        }
    }

    public static int safeGetRowCount(Segment seg) {
        try {
            return seg.isReadable() ? seg.getRowCount() : 0;
        } finally {
            releaseHisSeg(seg);
        }
    }
}
