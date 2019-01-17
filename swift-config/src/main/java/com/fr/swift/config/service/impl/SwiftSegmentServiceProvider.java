package com.fr.swift.config.service.impl;

import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.event.ClusterEvent;
import com.fr.swift.event.ClusterEventListener;
import com.fr.swift.event.ClusterEventType;
import com.fr.swift.event.ClusterListenerHandler;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.source.SourceKey;
import com.fr.third.org.hibernate.criterion.Criterion;
import com.fr.third.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/6/7
 */
@Service("segmentServiceProvider")
public class SwiftSegmentServiceProvider implements SwiftSegmentService {
    private SwiftClusterSegmentService service;

    public SwiftSegmentServiceProvider() {
        service = SwiftContext.get().getBean(SwiftClusterSegmentService.class);
        service.checkOldConfig();
        ClusterListenerHandler.addListener(new ClusterEventListener() {
            @Override
            public void handleEvent(ClusterEvent clusterEvent) {
                if (clusterEvent.getEventType() == ClusterEventType.JOIN_CLUSTER) {
                    String clusterId = ClusterSelector.getInstance().getFactory().getCurrentId();
                    service.setClusterId(clusterId);
                } else if (clusterEvent.getEventType() == ClusterEventType.LEFT_CLUSTER) {
                    service.setClusterId("LOCAL");
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
    public boolean removeSegments(List<SegmentKey> segmentKeys) {
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
    public Map<String, List<SegmentKey>> getOwnSegments() {
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
    public List<SegmentKey> find(Criterion... criterion) {
        return service.find(criterion);
    }

    @Override
    public boolean saveOrUpdate(SegmentKey obj) {
        return service.saveOrUpdate(obj);
    }

    @Override
    public Map<String, Map<String, List<SegmentKey>>> getAllSegLocations() {
        return service.getAllSegLocations();
    }
}
