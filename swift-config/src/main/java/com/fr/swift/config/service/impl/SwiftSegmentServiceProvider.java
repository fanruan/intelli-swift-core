package com.fr.swift.config.service.impl;

import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.event.ClusterEvent;
import com.fr.swift.event.ClusterEventListener;
import com.fr.swift.event.ClusterEventType;
import com.fr.swift.event.ClusterListenerHandler;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.selector.ClusterSelector;
import com.fr.third.org.hibernate.criterion.Criterion;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.beans.factory.annotation.Qualifier;
import com.fr.third.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/6/7
 */
@Service("segmentServiceProvider")
public class SwiftSegmentServiceProvider implements SwiftSegmentService {
    @Autowired
    @Qualifier("swiftSegmentService")
    private SwiftSegmentService service;

    public SwiftSegmentServiceProvider() {
        service = SwiftContext.get().getBean("swiftSegmentService", SwiftSegmentService.class);
        ClusterListenerHandler.addListener(new ClusterEventListener() {
            @Override
            public void handleEvent(ClusterEvent clusterEvent) {
                if (clusterEvent.getEventType() == ClusterEventType.JOIN_CLUSTER) {
                    String clusterId = ClusterSelector.getInstance().getFactory().getCurrentId();
                    SwiftClusterSegmentServiceImpl clusterService = SwiftContext.get().getBean(SwiftClusterSegmentServiceImpl.class);
                    clusterService.setClusterId(clusterId);
                    service = clusterService;
                } else if (clusterEvent.getEventType() == ClusterEventType.LEFT_CLUSTER) {
                    service = SwiftContext.get().getBean("swiftSegmentService", SwiftSegmentService.class);
                }
            }
        });
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
    public boolean removeSegments(SegmentKey... segmentKeys) {
        return service.removeSegments(segmentKeys);
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
    public Map<String, List<SegmentKey>> getAllRealTimeSegments() {
        return service.getAllRealTimeSegments();
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
    public List<SegmentKey> find(Criterion... criterion) {
        return service.find(criterion);
    }

    @Override
    public boolean saveOrUpdate(SegmentKey obj) {
        return service.saveOrUpdate(obj);
    }
}
