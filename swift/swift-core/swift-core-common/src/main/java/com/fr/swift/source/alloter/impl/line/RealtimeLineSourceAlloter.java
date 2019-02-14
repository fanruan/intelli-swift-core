package com.fr.swift.source.alloter.impl.line;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.cube.CubePathBuilder;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.alloter.impl.SwiftSegmentInfo;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author anchore
 * @date 2018/12/21
 */
public class RealtimeLineSourceAlloter extends BaseLineSourceAlloter {

    private static final SwiftMetaDataService metaDataService = SwiftContext.get().getBean(SwiftMetaDataService.class);

    public RealtimeLineSourceAlloter(SourceKey tableKey, LineAllotRule rule) {
        super(tableKey, rule);
    }

    @Override
    protected SegmentState getInsertableSeg() {
        Map<SourceKey, List<SegmentKey>> keyListMap = SEG_SVC.getOwnSegments();
        List<SegmentKey> segKeys = keyListMap.get(tableKey);
        segKeys = segKeys == null ? Collections.<SegmentKey>emptyList() : segKeys;

        SegmentKey maxSegKey = null;
        int maxRowCount = 0;

        for (SegmentKey segKey : segKeys) {
            if (segKey.getStoreType().isPersistent()) {
                continue;
            }
            if (isSegInserting(new SwiftSegmentInfo(segKey.getOrder(), segKey.getStoreType()))) {
                continue;
            }
            Segment seg = newRealTimeSeg(segKey);
            int rowCount = seg.isReadable() ? seg.getRowCount() : 0;

            // todo 暂时限制为未满的块，解除限制会出别的问题
            if (rowCount < rule.getCapacity() && rowCount > maxRowCount) {
                // 这边假设配置中可能存在多个realTimeSegment的情况下，取出行数最多的segment进行插入
                maxSegKey = segKey;
                maxRowCount = rowCount;
            }
        }
        if (maxSegKey == null) {
            maxSegKey = SEG_SVC.tryAppendSegment(tableKey, Types.StoreType.MEMORY);
        }
        SwiftSegmentInfo segInfo = new SwiftSegmentInfo(maxSegKey.getOrder(), maxSegKey.getStoreType());
        return new SegmentState(segInfo, maxRowCount - 1);
    }

    private Segment newRealTimeSeg(SegmentKey key) {
        IResourceLocation location = new ResourceLocation(new CubePathBuilder(key).build(), key.getStoreType());
        SwiftMetaData metaData = metaDataService.getMetaDataByKey(tableKey.getId());
        return SwiftContext.get().getBean("realtimeSegment", Segment.class, location, metaData);
    }
}