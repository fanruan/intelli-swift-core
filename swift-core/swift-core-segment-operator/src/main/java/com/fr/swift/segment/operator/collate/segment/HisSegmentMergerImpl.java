package com.fr.swift.segment.operator.collate.segment;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.entity.SwiftSegmentBucketElement;
import com.fr.swift.config.service.SwiftSegmentBucketService;
import com.fr.swift.config.service.SwiftSegmentLocationService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.cube.CubePathBuilder;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.CacheColumnSegment;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.alloter.AllotRule;
import com.fr.swift.source.alloter.SwiftSourceAlloter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lyon
 * @date 2019/2/20
 */
public class HisSegmentMergerImpl implements HisSegmentMerger {

    private static final SwiftSegmentService SEG_SVC = SwiftContext.get().getBean(SwiftSegmentService.class);
    private static final SwiftSegmentBucketService BUCKET_SVC = SwiftContext.get().getBean(SwiftSegmentBucketService.class);
    private static final int currentDir = 0;
    private static final int LINE_VIRTUAL_INDEX = -1;

    @Override
    public List<SegmentKey> merge(DataSource dataSource, List<Segment> segments, SwiftSourceAlloter alloter, int index) {
        AllotRule rule = alloter.getAllotRule();
        Partitioner partitioner = new LinePartitioner(rule.getCapacity());
        List<SegmentKey> segmentKeys = new ArrayList<SegmentKey>();
        List<String> fields = dataSource.getMetadata().getFieldNames();
        List<SegmentItem> items = partitioner.partition(segments);
        try {
            for (SegmentItem item : items) {
                SegmentKey segKey = SEG_SVC.tryAppendSegment(dataSource.getSourceKey(), Types.StoreType.FINE_IO);
                segmentKeys.add(segKey);
                ResourceLocation location = new ResourceLocation(new CubePathBuilder(segKey).setTempDir(currentDir).build(), segKey.getStoreType());
                Segment segment = new CacheColumnSegment(location, dataSource.getMetadata());
                try {
                    Builder builder = new SegmentBuilder(segment, fields, item.getSegments(), item.getAllShow());
                    builder.build();
                    SegmentUtils.releaseHisSeg(segment);
//                    SEG_LOCATION_SVC.saveOnNode(SwiftProperty.getProperty().getMachineId(), Collections.singleton(segKey));
                } catch (Throwable e) {
                    try {
                        SegmentUtils.releaseHisSeg(segment);
                        SEG_SVC.delete(segmentKeys);
                        for (SegmentKey key : segmentKeys) {
                            SegmentUtils.clearSegment(key);
                        }
                    } catch (Exception ignore) {
                        SwiftLoggers.getLogger().error("ignore exception", ignore);
                    }
                    SwiftLoggers.getLogger().error("merge", e);
                    return new ArrayList<>();
                }
            }
            if (index != LINE_VIRTUAL_INDEX) {
                for (SegmentKey segmentKey : segmentKeys) {
                    SwiftSegmentBucketElement element = new SwiftSegmentBucketElement(dataSource.getSourceKey(), index, segmentKey.getId());
                    BUCKET_SVC.save(element);
                }
            }
            return segmentKeys;
        } finally {
            // 释放读过的碎片块。这边直接释放碎片块，可能会导致还在session中的查询报错，刷新之后就没问题了
            for (SegmentItem item : items) {
                SegmentUtils.releaseHisSeg(item.getSegments());
            }
        }
    }

}
