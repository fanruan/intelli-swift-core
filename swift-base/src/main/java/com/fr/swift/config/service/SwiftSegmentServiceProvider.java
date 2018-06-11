package com.fr.swift.config.service;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.segment.SegmentKey;

import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/6/7
 */
public class SwiftSegmentServiceProvider implements SwiftSegmentService {

    private SwiftSegmentService service;

    private SwiftSegmentServiceProvider() {
        this.service = SwiftContext.getInstance().getBean("swiftSegmentService", SwiftSegmentService.class);
    }

    public static SwiftSegmentServiceProvider getProvider() {
        return SingletonHolder.provider;
    }

    public void setService(SwiftSegmentService service) {
        this.service = service;
    }

    @Override
    public boolean addSegments(List<SegmentKey> segments) {
        return service.addSegments(segments);
    }

    @Override
    public boolean removeSegments(String... sourceKey) {
        return service.removeSegments(sourceKey);
    }

    @Override
    public boolean updateSegments(String sourceKey, List<SegmentKey> segments) {
        return service.updateSegments(sourceKey, segments);
    }

    @Override
    public Map<String, List<SegmentKey>> getAllSegments() {
        return service.getAllSegments();
    }

    @Override
    public List<SegmentKey> getSegmentByKey(String sourceKey) {
        return service.getSegmentByKey(sourceKey);
    }

    @Override
    public boolean containsSegment(SegmentKey segmentKey) {
        return service.containsSegment(segmentKey);
    }

    private static class SingletonHolder {
        private static SwiftSegmentServiceProvider provider = new SwiftSegmentServiceProvider();
    }
}
