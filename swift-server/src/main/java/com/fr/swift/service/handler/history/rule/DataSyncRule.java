package com.fr.swift.service.handler.history.rule;

import com.fr.swift.config.service.SwiftSegmentServiceProvider;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.impl.SegmentDestinationImpl;
import com.fr.swift.service.HistoryService;
import com.fr.swift.structure.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yee
 * @date 2018/6/11
 */
public interface DataSyncRule {
    DataSyncRule DEFAULT = new DataSyncRule() {

        @Override
        public Map<String, Set<SegmentKey>> calculate(Map<String, List<SegmentKey>> exists, Map<String, List<SegmentKey>> needLoad,
                                                      Map<String, Pair<Integer, List<SegmentDestination>>> destinations) {

            Set<String> historyNodes = exists.keySet();

            int lessCount = historyNodes.size() - 1;

            // 如果历史节点只有1个，数据至少存在一份，否则至少存在节点数-1份
            lessCount = lessCount < 1 ? 1 : lessCount;


            Map<String, Map<String, AtomicInteger>> map = calculateNeedLoad(exists, needLoad, destinations);

            Set<String> needLoadSource = needLoad.keySet();
            Map<String, Set<SegmentKey>> result = new HashMap<String, Set<SegmentKey>>();

            for (String s : needLoadSource) {
                List<SegmentKey> needLoadList = needLoad.get(s);
                Iterator<SegmentKey> iterator = needLoadList.iterator();
                while (iterator.hasNext()) {
                    SegmentKey segmentKey = iterator.next();
                    Map<String, AtomicInteger> readyToSort = map.get(s);
                    if (null == readyToSort) {
                        readyToSort = new HashMap<String, AtomicInteger>();
                        map.put(s, readyToSort);
                    }
                    List<SegmentPair> sort = sort(readyToSort);
                    // 每个表对应的segment数最少的 lessCount个节点加载一个Segment
                    for (int i = 0; i < lessCount; i++) {
                        if (sort.isEmpty()) {
                            Iterator<String> nodeIterator = historyNodes.iterator();
                            while (nodeIterator.hasNext()) {
                                String clusterId = nodeIterator.next();
                                if (null == map.get(s).get(clusterId)) {
                                    map.get(s).put(clusterId, new AtomicInteger(0));
                                }
                                map.get(s).get(clusterId).incrementAndGet();
                                if (result.get(clusterId) == null) {
                                    result.put(clusterId, new HashSet<SegmentKey>());
                                }
                                if (destinations.get(s) == null) {
                                    int segmentCount = SwiftSegmentServiceProvider.getProvider().getAllSegments().get(s).size();
                                    destinations.put(s, new Pair<Integer, List<SegmentDestination>>(segmentCount, new ArrayList<SegmentDestination>()));
                                }
                                destinations.get(s).getValue().add(new SegmentDestinationImpl(clusterId, segmentKey.getUri(), segmentKey.getOrder(), HistoryService.class, "historyQuery"));
                                result.get(clusterId).add(segmentKey);
                            }
                        } else {
                            String clusterId = sort.get(i).clusterId;
                            map.get(s).get(clusterId).incrementAndGet();
                            if (result.get(clusterId) == null) {
                                result.put(clusterId, new HashSet<SegmentKey>());
                            }
                            if (destinations.get(s) == null) {
                                destinations.put(s, new Pair<Integer, List<SegmentDestination>>(0, new ArrayList<SegmentDestination>()));
                            }
                            destinations.get(s).getValue().add(new SegmentDestinationImpl(clusterId, segmentKey.getUri(), segmentKey.getOrder(), HistoryService.class, "historyQuery"));
                            destinations.get(s).setKey(destinations.get(s).getKey() + 1);
                            result.get(clusterId).add(segmentKey);
                        }
                    }
                    iterator.remove();
                }
            }

            return result;
        }

        /**
         * 算出真正需要load的Segment，并且算出每个历史节点每个表包含的Segment数
         *
         * @param exists
         * @param needLoad
         * @return
         */
        private Map<String, Map<String, AtomicInteger>> calculateNeedLoad(Map<String, List<SegmentKey>> exists, Map<String, List<SegmentKey>> needLoad,
                                                                          Map<String, Pair<Integer, List<SegmentDestination>>> destinations) {
            Set<String> historyNodes = exists.keySet();
            final Map<String, Map<String, AtomicInteger>> result = new HashMap<String, Map<String, AtomicInteger>>();
            int lessCount = historyNodes.size() - 1;
            lessCount = lessCount < 1 ? 1 : lessCount;
            Map<SegmentKey, Integer> count = new HashMap<SegmentKey, Integer>();
            Map<String, List<SegmentDestination>> segCount = new HashMap<String, List<SegmentDestination>>();
            for (String historyNode : historyNodes) {
                List<SegmentKey> nodeSegmentKey = exists.get(historyNode);
                for (SegmentKey segmentKey : nodeSegmentKey) {
                    String sourceKey = segmentKey.getTable().getId();
                    if (null == result.get(sourceKey)) {
                        result.put(sourceKey, new HashMap<String, AtomicInteger>());
                    }
                    if (null == result.get(sourceKey).get(historyNode)) {
                        result.get(sourceKey).put(historyNode, new AtomicInteger(0));
                    }
                    // destination
                    if (null == destinations.get(sourceKey)) {
                        destinations.put(sourceKey, new Pair<Integer, List<SegmentDestination>>(0, new ArrayList<SegmentDestination>()));
                    }
                    SegmentDestination destination = new SegmentDestinationImpl(historyNode, segmentKey.getUri(), segmentKey.getOrder(), HistoryService.class, "historyQuery");
                    destinations.get(sourceKey).getValue().add(destination);
                    if (null == segCount.get(sourceKey)) {
                        segCount.put(sourceKey, new ArrayList<SegmentDestination>() {
                            @Override
                            public boolean add(SegmentDestination segmentDestination) {
                                return !contains(segmentDestination) && super.add(segmentDestination);
                            }
                        });
                    }
                    segCount.get(sourceKey).add(destination);
                    result.get(sourceKey).get(historyNode).incrementAndGet();
                    List<SegmentKey> newSegments = needLoad.get(sourceKey);
                    if (null != newSegments && newSegments.contains(segmentKey)) {
                        if (count.get(segmentKey) == null) {
                            count.put(segmentKey, 0);
                        }
                        int now = count.get(segmentKey);
                        if (++now >= lessCount) {
                            newSegments.remove(segmentKey);
                            needLoad.put(sourceKey, newSegments);
                        } else {
                            count.put(segmentKey, now);
                        }
                    }
                }
            }
            Iterator<String> iterator = segCount.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                destinations.get(key).setKey(segCount.get(key).size());
            }
            Iterator<String> it = result.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                for (String historyNode : historyNodes) {
                    if (result.get(key).get(historyNode) == null) {
                        result.get(key).put(historyNode, new AtomicInteger(0));
                    }
                }
            }
            return result;
        }

        private List<SegmentPair> sort(Map<String, AtomicInteger> readyToSort) {
            Iterator<Map.Entry<String, AtomicInteger>> iterator = readyToSort.entrySet().iterator();
            List<SegmentPair> keys = new ArrayList<SegmentPair>();
            while (iterator.hasNext()) {
                Map.Entry<String, AtomicInteger> entry = iterator.next();
                keys.add(new SegmentPair(entry.getKey(), entry.getValue().get()));
            }
            Collections.sort(keys);
            return keys;
        }

        class SegmentPair implements Comparable<SegmentPair> {

            private String clusterId;
            private int count;

            public SegmentPair(String clusterId, int count) {
                this.clusterId = clusterId;
                this.count = count;
            }

            @Override
            public int compareTo(SegmentPair o) {
                return count - o.count;
            }
        }
    };

    /**
     * 计算每个节点应该load哪些segment
     *
     * @param exists
     * @param needLoad
     * @return
     */
    Map<String, Set<SegmentKey>> calculate(Map<String, List<SegmentKey>> exists, Map<String, List<SegmentKey>> needLoad,
                                           Map<String, Pair<Integer, List<SegmentDestination>>> destinations);
}
