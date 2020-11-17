package com.fr.swift.segment.operator.collate.segment;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.entity.SwiftSegmentBucketElement;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author lyon
 * @date 2019/2/20
 */
public class HisSegmentMergerImpl implements HisSegmentMerger {

    private static final SwiftSegmentService SEG_SVC = SwiftContext.get().getBean(SwiftSegmentService.class);
    private static final int currentDir = 0;
    private static final int LINE_VIRTUAL_INDEX = -1;

    @Override
    public List<SegmentKey> merge(DataSource dataSource, List<SegmentPartition> segmentPartitions, int index) {
        List<SegmentKey> segmentKeys = new ArrayList<>();
        List<String> fields = dataSource.getMetadata().getFieldNames();
        try {
            for (SegmentPartition item : segmentPartitions) {
                Optional<String> first = item.getSegmentKeys().stream().map(SegmentKey::getSegmentUri).collect(Collectors.toSet()).stream().findFirst();
                SegmentKey segKey = SEG_SVC.tryAppendSegment(dataSource.getSourceKey(), Types.StoreType.FINE_IO, first.orElse(null));
                segmentKeys.add(segKey);
                ResourceLocation location = new ResourceLocation(new CubePathBuilder(segKey).setTempDir(segKey.getSegmentUri()).build(), segKey.getStoreType());
                Segment segment = new CacheColumnSegment(location, dataSource.getMetadata());
                try {
                    Builder builder = new SegmentBuilder(segment, fields, item.getSegments(), item.getAllShow());
                    builder.build();
                    SegmentUtils.releaseHisSeg(segment);
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
                SEG_SVC.saveBuckets(segmentKeys.stream().map(r -> new SwiftSegmentBucketElement(dataSource.getSourceKey(), index, r.getId())).collect(Collectors.toList()));
            }
            return segmentKeys;
        } finally {
            // 释放读过的碎片块。这边直接释放碎片块，可能会导致还在session中的查询报错，刷新之后就没问题了
            for (SegmentPartition item : segmentPartitions) {
                SegmentUtils.releaseHisSeg(item.getSegments());
            }
        }
    }

}
