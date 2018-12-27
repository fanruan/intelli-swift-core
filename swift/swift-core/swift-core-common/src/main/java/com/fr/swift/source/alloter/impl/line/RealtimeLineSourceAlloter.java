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
import com.fr.swift.source.alloter.impl.BaseSourceAlloter;
import com.fr.swift.source.alloter.impl.SwiftSegmentInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author anchore
 * @date 2018/12/21
 */
public class RealtimeLineSourceAlloter extends BaseSourceAlloter<LineAllotRule, LineRowInfo> {

    private static final SwiftMetaDataService metaDataService = SwiftContext.get().getBean(SwiftMetaDataService.class);

    public RealtimeLineSourceAlloter(SourceKey tableKey, LineAllotRule rule) {
        super(tableKey, rule);
    }

    @Override
    protected SegmentState append(int logicOrder) {
        SegmentState segState = createSegmentState();
        logicToReal.put(logicOrder, segState);
        return segState;
    }

    private SegmentState createSegmentState() {
        Map<SourceKey, List<SegmentKey>> keyListMap = SEG_SVC.getOwnSegments();
        List<SegmentKey> keys = keyListMap.get(tableKey);
        keys = keys == null ? new ArrayList<SegmentKey>() : keys;
        SegmentKey segmentKey = null;
        int rows = 0;
        for (SegmentKey key : keys) {
            if (key.getStoreType() == Types.StoreType.MEMORY) {
                Segment segment = newRealTimeSeg(key);
                int rowCount = 0;
                if (segment.isReadable()) {
                    rowCount = segment.getRowCount();
                }
                if (rowCount < rule.getCapacity() && rowCount >= rows) {
                    // 这边假设配置中可能存在多个realTimeSegment的情况下，取出行数最多的segment进行插入
                    segmentKey = key;
                    rows = rowCount;
                }
            }
        }
        if (segmentKey == null) {
            segmentKey = SEG_SVC.tryAppendSegment(tableKey, Types.StoreType.MEMORY);
        }
        SwiftSegmentInfo segInfo = new SwiftSegmentInfo(segmentKey.getOrder(), Types.StoreType.MEMORY);
        return new SegmentState(segInfo, rows - 1);
    }

    private Segment newRealTimeSeg(SegmentKey key) {
        IResourceLocation location = new ResourceLocation(new CubePathBuilder(key).asBackup().build(), key.getStoreType());
        SwiftMetaData metaData = metaDataService.getMetaDataByKey(tableKey.getId());
        return SwiftContext.get().getBean("realtimeSegment", Segment.class, location, metaData);
    }

    @Override
    protected int getLogicOrder(LineRowInfo rowInfo) {
        return 0;
    }
}