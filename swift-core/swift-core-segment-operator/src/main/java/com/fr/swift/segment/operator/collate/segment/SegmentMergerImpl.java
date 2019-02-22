package com.fr.swift.segment.operator.collate.segment;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.cube.CubePathBuilder;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.alloter.AllotRule;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.source.alloter.impl.BaseAllotRule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyon on 2019/2/20.
 */
public class SegmentMergerImpl implements SegmentMerger {

    private static final SwiftSegmentService SEG_SVC = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class);
    private static final int currentDir = 0;

    @Override
    public List<SegmentKey> merge(DataSource dataSource, List<Segment> segments, SwiftSourceAlloter alloter) {
        AllotRule rule = alloter.getAllotRule();
        Partitioner partitioner = null;
        if (rule.getType() == BaseAllotRule.AllotType.HASH) {
            partitioner = new HashPartitioner();
        } else {
            partitioner = new LinePartitioner(rule.getCapacity());
        }
        List<SegmentKey> segmentKeys = new ArrayList<SegmentKey>();
        List<String> fields = dataSource.getMetadata().getFieldNames();
        List<SegmentItem> items = partitioner.partition(segments);
        for (SegmentItem item : items) {
            SegmentKey segKey = SEG_SVC.tryAppendSegment(dataSource.getSourceKey(), Types.StoreType.FINE_IO);
            segmentKeys.add(segKey);
            ResourceLocation location = new ResourceLocation(new CubePathBuilder(segKey).setTempDir(currentDir).build(), segKey.getStoreType());
            Segment segment = SegmentUtils.newSegment(location, dataSource.getMetadata());
            try {
                AbstractBuilder builder = new SegmentBuilder(segment, fields, item.getSegments(), item.getAllShow());
                builder.build();
                SegmentUtils.release(segment);
            } catch (Throwable e) {
                try {
                    SegmentUtils.release(segment);
                    SEG_SVC.removeSegments(segmentKeys);
                    for (SegmentKey key : segmentKeys) {
                        SegmentUtils.clearSegment(key);
                    }
                } catch (Exception ignore) {
                }
                return new ArrayList<SegmentKey>();
            }
        }
        return segmentKeys;
    }

}
