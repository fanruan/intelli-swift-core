package com.fr.swift.segment.impl;

import com.fr.stable.StringUtils;
import com.fr.swift.config.bean.SegmentDestSelectRule;
import com.fr.swift.config.service.SegmentDestSelectRuleService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.segment.SegmentLocationManager;
import com.fr.swift.source.SourceKey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2018/6/13
 */
public class SegmentLocationManagerImpl implements SegmentLocationManager {

    private Map<String, HashMap<String, SegmentDestination>> remoteSegments;
    private Map<String, HashMap<String, SegmentDestination>> localSegments;
    private SegmentDestSelectRule rule;

    public SegmentLocationManagerImpl() {
        remoteSegments = new ConcurrentHashMap<String, HashMap<String, SegmentDestination>>();
        localSegments = new ConcurrentHashMap<String, HashMap<String, SegmentDestination>>();
        rule = SwiftContext.get().getBean(SegmentDestSelectRuleService.class).getCurrentRule();
    }

    public void setRule(SegmentDestSelectRule rule) {
        this.rule = rule;
    }

    @Override
    synchronized
    public List<SegmentDestination> getSegmentLocationURI(SourceKey table) {
        List<SegmentDestination> destinations = new ArrayList<SegmentDestination>();
        if (localSegments.containsKey(table.getId())) {
            destinations.addAll(localSegments.get(table.getId()).values());
        }
        if (remoteSegments.containsKey(table.getId())) {
            destinations.addAll(remoteSegments.get(table.getId()).values());
        }
        destinations = rule.selectDestination(destinations);
        // 暂时先这么处理，，，，
        if (null == destinations || destinations.isEmpty()) {
            destinations = new ArrayList<SegmentDestination>();
            destinations.add(new SegmentDestinationImpl(StringUtils.EMPTY, 0));
        }
        return destinations;
    }

    @Override
    public synchronized void updateSegmentInfo(SegmentLocationInfo segmentInfo, SegmentLocationInfo.UpdateType updateType) {
        if (updateType == SegmentLocationInfo.UpdateType.ALL) {
            remoteSegments.clear();
        }
        Map<String, List<SegmentDestination>> map = segmentInfo.getDestinations();
        for (Map.Entry<String, List<SegmentDestination>> entry : map.entrySet()) {
            String sourceKey = entry.getKey();
            if (!localSegments.containsKey(sourceKey)) {
                localSegments.put(sourceKey, new HashMap<String, SegmentDestination>());
            }
            if (!remoteSegments.containsKey(sourceKey)) {
                remoteSegments.put(sourceKey, new HashMap<String, SegmentDestination>());
            }

            List<SegmentDestination> list = new ArrayList<SegmentDestination>(entry.getValue());
            Collections.sort(list);
            for (SegmentDestination destination : list) {
                if (!destination.isRemote()) {
                    calculateContains(localSegments, sourceKey, destination);
                } else {
                    if (localSegments.get(sourceKey).containsKey(destination.getSegmentId())) {
                        localSegments.get(sourceKey).get(destination.getSegmentId()).getSpareNodes().add(destination.getClusterId());
                    } else if (!calculateContains(remoteSegments, sourceKey, destination)) {
                        remoteSegments.get(sourceKey).get(destination.getSegmentId()).getSpareNodes().add(destination.getClusterId());
                    }
                }
            }
        }
    }

    private boolean calculateContains(Map<String, HashMap<String, SegmentDestination>> map, String sourceKey, SegmentDestination destination) {
        if (!map.get(sourceKey).containsKey(destination.getSegmentId())) {
            map.get(sourceKey).put(destination.getSegmentId(), destination);
            return true;
        }
        return false;
    }

    @Override
    public Map<String, List<SegmentDestination>> getSegmentInfo() {
        Map<String, List<SegmentDestination>> target = new HashMap<String, List<SegmentDestination>>();
        map2List(target, localSegments);
        map2List(target, remoteSegments);
        return target;
    }

    private void map2List(Map<String, List<SegmentDestination>> target, Map<String, HashMap<String, SegmentDestination>> segments) {
        Iterator<String> localIterator = segments.keySet().iterator();
        while (localIterator.hasNext()) {
            String key = localIterator.next();
            if (null == target.get(key)) {
                target.put(key, new ArrayList<SegmentDestination>());
            }
            Map<String, SegmentDestination> map = segments.get(key);
            for (SegmentDestination value : map.values()) {
                List<String> spares = new ArrayList<String>(value.getSpareNodes());
                value.getSpareNodes().clear();
                target.get(key).add(value);
                for (String spare : spares) {
                    SegmentDestination copy = value.copy();
                    copy.setClusterId(spare);
                    target.get(key).add(copy);
                }
            }

        }
    }

    @Override
    synchronized
    public void removeTable(String sourceKey) {
        remoteSegments.remove(sourceKey);
    }

    @Override
    synchronized
    public void removeSegment(String sourceKey, List<String> segmentKeys) {
        if (remoteSegments.containsKey(sourceKey)) {
            for (String segmentKey : segmentKeys) {
                remoteSegments.get(sourceKey).remove(segmentKey);
            }
        }

        if (localSegments.containsKey(sourceKey)) {
            for (String segmentKey : segmentKeys) {
                localSegments.get(sourceKey).remove(segmentKey);
            }
        }
    }

}
