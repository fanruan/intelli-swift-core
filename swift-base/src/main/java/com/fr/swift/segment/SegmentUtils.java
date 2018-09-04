package com.fr.swift.segment;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.source.SwiftMetaData;

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

    public static SegmentKey getMaxSegmentKey(List<SegmentKey> segmentKeys) {
        if (segmentKeys == null || segmentKeys.isEmpty()) {
            return null;
        } else {
            SegmentKey maxSegmentKey = segmentKeys.get(0);
            for (SegmentKey segmentKey : segmentKeys) {
                if (segmentKey.getOrder() > maxSegmentKey.getOrder()) {
                    maxSegmentKey = segmentKey;
                }
            }
            return maxSegmentKey;
        }
    }
}
