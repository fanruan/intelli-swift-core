package com.fr.swift.config.service.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.oper.ConfigWhere;
import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;

import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/6/7
 */
@SwiftBean(name = "segmentServiceProvider")
public class SwiftSegmentServiceProvider implements SwiftSegmentService {
    private SwiftClusterSegmentService service = SwiftContext.get().getBean(SwiftClusterSegmentService.class);

    @Override
    public boolean addSegments(List<SegmentKey> segments) {
        return service.addSegments(segments);
    }

    @Override
    public boolean removeSegments(String... sourceKey) {
        return service.removeSegments(sourceKey);
    }

    @Override
    public boolean removeSegments(List<SegmentKey> segmentKeys) {
        return service.removeSegments(segmentKeys);
    }

    @Override
    public boolean updateSegments(String sourceKey, List<SegmentKey> segments) {
        return service.updateSegments(sourceKey, segments);
    }

    @Override
    public Map<SourceKey, List<SegmentKey>> getAllSegments() {
        return service.getAllSegments();
    }

    @Override
    public Map<SourceKey, List<SegmentKey>> getOwnSegments() {
        return service.getOwnSegments();
    }

    @Override
    public List<SegmentKey> getSegmentByKey(String sourceKey) {
        return service.getSegmentByKey(sourceKey);
    }

    @Override
    public boolean containsSegment(SegmentKey segmentKey) {
        return service.containsSegment(segmentKey);
    }

    @Override
    public SegmentKey tryAppendSegment(SourceKey tableKey, StoreType storeType) {
        return service.tryAppendSegment(tableKey, storeType);
    }

    @Override
    public List<SegmentKey> find(ConfigWhere... criterion) {
        return service.find(criterion);
    }

    @Override
    public boolean saveOrUpdate(SegmentKey obj) {
        return service.saveOrUpdate(obj);
    }
}
