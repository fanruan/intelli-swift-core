package com.fr.swift.segment;

import com.fineio.FineIO;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.cube.io.ResourceDiscovery;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.operator.column.SwiftColumnDictMerger;
import com.fr.swift.segment.operator.column.SwiftColumnIndexer;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.FileUtil;
import com.fr.swift.util.function.Predicate;
import com.fr.third.guava.base.Optional;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018/7/10
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SegmentUtils {
    public static Segment newSegment(SegmentKey segKey) {
        SwiftMetaData meta = SwiftDatabase.getInstance().getTable(segKey.getTable()).getMetadata();
        String segPath = CubeUtil.getSegPath(segKey);

        if (segKey.getStoreType() == StoreType.MEMORY) {
            return newRealtimeSegment(new ResourceLocation(segPath, segKey.getStoreType()), meta);
        }
        return newHistorySegment(new ResourceLocation(segPath, segKey.getStoreType()), meta);
    }

    public static Segment newRealtimeSegment(IResourceLocation location, SwiftMetaData meta) {
        return (Segment) SwiftContext.get().getBean("realtimeSegment", location, meta);
    }

    public static Segment newHistorySegment(IResourceLocation location, SwiftMetaData meta) {
        return (Segment) SwiftContext.get().getBean("historySegment", location, meta);
    }

    /**
     * 只清数据，不清配置
     *
     * @param segKey seg key
     */
    public static void clearSegment(SegmentKey segKey) {
        if (segKey.getStoreType() == StoreType.MEMORY) {
            clearRealtimeSegment(segKey);
        } else {
            clearHistorySegment(segKey);
        }

    }

    private static void clearRealtimeSegment(SegmentKey segKey) {
        final String segPath = CubeUtil.getSegPath(segKey);

        ResourceDiscovery.getInstance().removeIf(new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return s.contains(segPath);
            }
        });

        FileUtil.delete(CubeUtil.getAbsoluteSegPath(segKey).replace(segKey.getSwiftSchema().getDir(), segKey.getSwiftSchema().getBackupDir()));
    }

    private static void clearHistorySegment(SegmentKey segKey) {
        FileUtil.delete(CubeUtil.getAbsoluteSegPath(segKey));
    }

    public static void indexSegmentIfNeed(List<Segment> segs) throws Exception {
        final List<Segment> hisSegs = new ArrayList<Segment>();
        for (Segment seg : segs) {
            if (seg.isHistory()) {
                hisSegs.add(seg);
            }
        }

        if (hisSegs.isEmpty()) {
            return;
        }

        final SwiftMetaData metadata = hisSegs.get(0).getMetaData();
        for (int i = 0; i < metadata.getColumnCount(); i++) {
            final ColumnKey columnKey = new ColumnKey(metadata.getColumnName(i + 1));

            ((SwiftColumnIndexer) SwiftContext.get().getBean("columnIndexer", metadata, columnKey, hisSegs)).buildIndex();

            FineIO.doWhenFinished(new Runnable() {
                @Override
                public void run() {
                    try {
                        ((SwiftColumnDictMerger) SwiftContext.get().getBean("columnDictMerger", metadata, columnKey, hisSegs)).mergeDict();
                    } catch (Exception e) {
                        SwiftLoggers.getLogger().error(e);
                    }
                }
            });

        }
    }

    public static Optional<SegmentKey> getMaxSegmentKey(List<SegmentKey> segmentKeys) {
        if (segmentKeys == null || segmentKeys.isEmpty()) {
            return Optional.absent();
        }
        SegmentKey maxSegmentKey = segmentKeys.get(0);
        for (SegmentKey segmentKey : segmentKeys) {
            if (segmentKey.getOrder() > maxSegmentKey.getOrder()) {
                maxSegmentKey = segmentKey;
            }
        }
        return Optional.of(maxSegmentKey);
    }

    public static void release(Segment seg) {
        if (seg != null && seg.isHistory()) {
            seg.release();
        }
    }

    public static void release(Iterable<Segment> segs) {
        if (segs == null) {
            return;
        }
        for (Segment seg : segs) {
            release(seg);
        }
    }

    public static <T> void release(Column<T> column) {
        if (column != null && column.getLocation().getStoreType().isPersistent()) {
            column.getDetailColumn().release();
            column.getDictionaryEncodedColumn().release();
            column.getBitmapIndex().release();
        }
    }

    public static <T> void releaseColumns(Iterable<Column<T>> columns) {
        if (columns == null) {
            return;
        }
        for (Column<T> column : columns) {
            release(column);
        }
    }
}
