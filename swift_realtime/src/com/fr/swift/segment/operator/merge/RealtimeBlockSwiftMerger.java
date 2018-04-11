package com.fr.swift.segment.operator.merge;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.config.ISegmentKey;
import com.fr.swift.config.unique.SegmentKeyUnique;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.segment.HistorySegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;

import java.util.Map;

/**
 * This class created on 2018/3/28
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class RealtimeBlockSwiftMerger extends AbstractMerger {

    public RealtimeBlockSwiftMerger(SourceKey sourceKey, SwiftMetaData metaData, String cubeSourceKey) {
        super(sourceKey, metaData, cubeSourceKey);
    }

    @Override
    protected Segment createSegment(int order) {
        String cubePath = System.getProperty("user.dir") + "/cubes/" + cubeSourceKey + "/seg" + order;
        IResourceLocation location = new ResourceLocation(cubePath);
        ISegmentKey segmentKey = new SegmentKeyUnique();
        segmentKey.setSegmentOrder(order);
        segmentKey.setUri(location.getUri().getPath());
        segmentKey.setSourceId(sourceKey.getId());
        segmentKey.setStoreType(Types.StoreType.FINE_IO.name());
        configSegment.addSegment(segmentKey);
        return new HistorySegmentImpl(location, metaData);
    }

    @Override
    public void release() {
        super.release();
        for (Map.Entry<Integer, Segment> entry : segmentIndexCache.getNewSegMap().entrySet()) {
            Segment segment = entry.getValue();
            segment.putAllShowIndex(BitMaps.newAllShowBitMap(segmentIndexCache.getSegRowByIndex(entry.getKey())));
            segment.putRowCount(segmentIndexCache.getSegRowByIndex(entry.getKey()));
            for (String field : metaData.getFieldNames()) {
                segment.getColumn(new ColumnKey(field)).getBitmapIndex().putNullIndex(segmentIndexCache.getNullBySegAndField(entry.getKey(), field));
                segment.getColumn(new ColumnKey(field)).getBitmapIndex().release();
                segment.getColumn(new ColumnKey(field)).getDetailColumn().release();
            }
            segment.release();
        }
    }
}
