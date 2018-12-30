package com.fr.swift.segment.bean.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.SegmentDestSelectRule;
import com.fr.swift.config.service.SegmentDestSelectRuleService;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.segment.SegmentLocationManager;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2018/10/7
 */
public abstract class AbstractSegmentLocationManager implements SegmentLocationManager {
    protected Map<SourceKey, CheckRemoveHashMap> remoteSegments;
    protected Map<SourceKey, CheckRemoveHashMap> localSegments;
    private SegmentDestSelectRule rule;

    public AbstractSegmentLocationManager() {
        remoteSegments = new ConcurrentHashMap<SourceKey, CheckRemoveHashMap>();
        localSegments = new ConcurrentHashMap<SourceKey, CheckRemoveHashMap>();
        rule = SwiftContext.get().getBean(SegmentDestSelectRuleService.class).getCurrentRule();
    }

    public void setRule(SegmentDestSelectRule rule) {
        this.rule = rule;
    }

    @Override
    synchronized
    public List<SegmentDestination> getSegmentLocationURI(SourceKey table) {
        List<SegmentDestination> destinations = new ArrayList<SegmentDestination>();
        if (localSegments.containsKey(table)) {
            destinations.addAll(filterNegative(localSegments.get(table).values()));
        }
        if (remoteSegments.containsKey(table)) {
            destinations.addAll(filterNegative(remoteSegments.get(table).values()));
        }
        Collections.sort(destinations);
        destinations = rule.selectDestination(destinations);
        return destinations;
    }

    @Override
    public synchronized void updateSegmentInfo(SegmentLocationInfo segmentInfo, SegmentLocationInfo.UpdateType updateType) {
        if (updateType == SegmentLocationInfo.UpdateType.ALL) {
            remoteSegments.clear();
        }
        Map<SourceKey, List<SegmentDestination>> map = segmentInfo.getDestinations();
        for (Map.Entry<SourceKey, List<SegmentDestination>> entry : map.entrySet()) {
            SourceKey sourceKey = entry.getKey();
            if (!localSegments.containsKey(sourceKey)) {
                localSegments.put(sourceKey, new CheckRemoveHashMap());
            }
            if (!remoteSegments.containsKey(sourceKey)) {
                remoteSegments.put(sourceKey, new CheckRemoveHashMap());
            }

            List<SegmentDestination> list = new ArrayList<SegmentDestination>(entry.getValue());
            Collections.sort(list);
            for (SegmentDestination destination : list) {
                if (!destination.isRemote()) {
                    calculateContains(localSegments, sourceKey, destination);
                } else {
                    calculateRemoteSegment(sourceKey, destination);
                }
            }
        }
    }

    protected abstract void calculateRemoteSegment(SourceKey sourceKey, SegmentDestination destination);

    protected boolean calculateContains(Map<SourceKey, CheckRemoveHashMap> map, SourceKey sourceKey, SegmentDestination destination) {
        if (!map.get(sourceKey).containsKey(destination.getSegmentId())) {
            map.get(sourceKey).put(destination.getSegmentId(), destination);
            return true;
        }
        return false;
    }

    @Override
    public Map<SourceKey, List<SegmentDestination>> getSegmentInfo() {
        Map<SourceKey, List<SegmentDestination>> target = new HashMap<SourceKey, List<SegmentDestination>>();
        map2List(target, localSegments);
        map2List(target, remoteSegments);
        return target;
    }

    private void map2List(Map<SourceKey, List<SegmentDestination>> target, Map<SourceKey, CheckRemoveHashMap> segments) {
        Iterator<SourceKey> localIterator = segments.keySet().iterator();
        while (localIterator.hasNext()) {
            SourceKey key = localIterator.next();
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
    public void removeTable(String cluster, SourceKey sourceKey) {
        removeSegments(cluster, sourceKey, null, localSegments);
        removeSegments(cluster, sourceKey, null, remoteSegments);
    }

    @Override
    synchronized
    public void removeSegments(final String cluster, SourceKey sourceKey, List<String> segmentKeys) {
        removeSegments(cluster, sourceKey, segmentKeys, remoteSegments);
        removeSegments(cluster, sourceKey, segmentKeys, localSegments);
    }

    private void removeSegments(final String cluster, SourceKey sourceKey, List<String> segmentKeys, Map<SourceKey, CheckRemoveHashMap> remoteSegments) {
        if (remoteSegments.containsKey(sourceKey)) {
            // 传空删全部
            if (null == segmentKeys || segmentKeys.isEmpty()) {
                segmentKeys = new ArrayList<String>(remoteSegments.get(sourceKey).keySet());
            }
            for (String segmentKey : segmentKeys) {
                remoteSegments.get(sourceKey).remove(segmentKey, new CheckRemoveHashMap.Check() {
                    @Override
                    public boolean check(SegmentDestination destination) {
                        if (null != destination) {
                            return Util.equals(cluster, destination.getClusterId());
                        }
                        return false;
                    }
                });
            }
        }
    }

    private List<SegmentDestination> filterNegative(Collection<SegmentDestination> collection) {
        List<SegmentDestination> list = new ArrayList<SegmentDestination>(collection);
        List<SegmentDestination> destinations = new ArrayList<SegmentDestination>();
        Collections.sort(list);
        if (list.isEmpty()) {
            return destinations;
        }
        List<String> negativeSegments = new ArrayList<String>();
        for (SegmentDestination destination : list) {
            String clusterId = destination.getClusterId();
            if (negativeSegments.contains(clusterId)) {
                continue;
            }
            if (destination.getOrder() == -1) {
                negativeSegments.add(clusterId);
            }
            destinations.add(destination);
        }
        return destinations;
    }

    static class CheckRemoveHashMap extends HashMap<String, SegmentDestination> {
        public void remove(String key, Check check) {
            if (check.check(get(key))) {
                remove(key);
            }
        }

        private interface Check {
            boolean check(SegmentDestination destination);
        }
    }
}
