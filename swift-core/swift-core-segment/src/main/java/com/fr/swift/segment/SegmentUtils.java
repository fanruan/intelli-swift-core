package com.fr.swift.segment;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.cube.CubePathBuilder;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.impl.base.ResourceDiscovery;
import com.fr.swift.segment.operator.column.SwiftColumnIndexer;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.FileUtil;
import com.fr.swift.util.IoUtil;
import com.fr.swift.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class created on 2018/7/10
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SegmentUtils {

    public static List<Segment> newSegments(List<SegmentKey> segKeys) {
        if (segKeys == null || segKeys.isEmpty()) {
            return Collections.emptyList();
        }

        List<Segment> segmentList = new ArrayList<Segment>();
        for (SegmentKey segKey : segKeys) {
            segmentList.add(newSegment(segKey));
        }
        return segmentList;
    }

    public static Segment newSegment(SegmentKey segKey) {
        return newSegment(segKey, CubeUtil.getCurrentDir(segKey.getTable()));
    }

    public static Segment newSegment(SegmentKey segmentKey, int tmpPath) {
        Util.requireNonNull(segmentKey);
        String cubePath;
        if (segmentKey.getStoreType().isTransient()) {
            cubePath = new CubePathBuilder(segmentKey).build();
        } else {
            cubePath = new CubePathBuilder(segmentKey).setTempDir(tmpPath).build();
        }
        Types.StoreType storeType = segmentKey.getStoreType();
        ResourceLocation location = new ResourceLocation(cubePath, storeType);
        SourceKey sourceKey = segmentKey.getTable();
        SwiftMetaData metaData = SwiftContext.get().getBean(SwiftMetaDataService.class).getMetaDataByKey(sourceKey.getId());
        Util.requireNonNull(metaData);
        return SegmentUtils.newSegment(location, metaData);
    }

    public static Segment newSegment(IResourceLocation location, SwiftMetaData meta) {
        if (location.getStoreType().isTransient()) {
            return SwiftContext.get().getBean("realtimeSegment", Segment.class, location, meta);
        }
        return SwiftContext.get().getBean("historySegment", Segment.class, location, meta);
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

    private static void clearRealtimeSegment(SegmentKey segKey) {
        ResourceDiscovery.getInstance().releaseSegment(segKey.getSwiftSchema(), segKey.getTable(), segKey.getOrder());

        FileUtil.delete(new CubePathBuilder(segKey).asAbsolute().asBackup().build());
    }

    private static void clearHistorySegment(SegmentKey segKey) {
        int currentDir = CubeUtil.getCurrentDir(segKey.getTable());
        FileUtil.delete(new CubePathBuilder(segKey).asAbsolute().setTempDir(currentDir).build());
    }

    public static void indexSegmentIfNeed(List<Segment> segs) throws Exception {
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
            try {
                SwiftContext.get().getBean("columnIndexer", SwiftColumnIndexer.class, columnKey, hisSegs).buildIndex();
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
            }
        }
    }

    /**
     * 只release非内存seg todo 可能需要更明确的方法名
     *
     * @param seg 块
     */
    public static void release(Segment seg) {
        if (seg != null && seg.isHistory()) {
            IoUtil.release(seg);
        }
    }

    public static void release(List<Segment> segs) {
        if (segs == null) {
            return;
        }
        for (Segment seg : segs) {
            release(seg);
        }
    }

    /**
     * 只release非内存seg todo 可能需要更明确的方法名
     *
     * @param column 列
     * @param <T>    数据类型
     */
    public static <T> void release(Column<T> column) {
        if (column != null && column.getLocation().getStoreType().isPersistent()) {
            IoUtil.release(column.getDetailColumn());
            IoUtil.release(column.getDictionaryEncodedColumn());
            IoUtil.release(column.getBitmapIndex());
        }
    }

    public static <T> void releaseColumns(List<Column<T>> columns) {
        if (columns == null) {
            return;
        }
        for (Column<T> column : columns) {
            release(column);
        }
    }

    public static void releaseColumnsOf(Segment seg) {
        try {
            SwiftMetaData meta = seg.getMetaData();
            for (int i = 0; i < meta.getColumnCount(); i++) {
                Column<?> column = seg.getColumn(new ColumnKey(meta.getColumnName(i + 1)));
                release(column);
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }
}
