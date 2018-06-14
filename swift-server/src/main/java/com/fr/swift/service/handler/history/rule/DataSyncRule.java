package com.fr.swift.service.handler.history.rule;

import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.impl.SegmentDestinationImpl;
import com.fr.swift.service.HistoryService;

import java.net.URI;
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
        public Map<String, Set<URI>> calculate(Map<String, List<SegmentKey>> exists, Map<String, List<SegmentKey>> needLoad,
                                               Map<String, List<SegmentDestination>> destinations) {

            Set<String> historyNodes = exists.keySet();

            int lessCount = historyNodes.size() - 1;

            // 如果历史节点只有1个，数据至少存在一份，否则至少存在节点数-1份
            lessCount = lessCount < 1 ? 1 : lessCount;


            Map<String, Map<String, AtomicInteger>> map = calculateNeedLoad(exists, needLoad, destinations);

            Set<String> needLoadSource = needLoad.keySet();
            Map<String, Set<URI>> result = new HashMap<String, Set<URI>>();

            for (String s : needLoadSource) {
                List<SegmentKey> needLoadList = needLoad.get(s);
                Iterator<SegmentKey> iterator = needLoadList.iterator();
                while (iterator.hasNext()) {
                    SegmentKey segmentKey = iterator.next();
                    Map<String, AtomicInteger> readyToSort = map.get(s);
                    List<Pair> sort = sort(readyToSort);
                    // 每个表对应的segment数最少的 lessCount个节点加载一个Segment
                    for (int i = 0; i < lessCount; i++) {
                        String clusterId = sort.get(i).clusterId;
                        map.get(s).get(clusterId).incrementAndGet();
                        if (result.get(clusterId) == null) {
                            result.put(clusterId, new HashSet<URI>());
                        }
                        if (destinations.get(s) == null) {
                            destinations.put(s, new ArrayList<SegmentDestination>());
                        }
                        destinations.get(s).add(new SegmentDestinationImpl(clusterId, segmentKey.getUri(), segmentKey.getOrder(), HistoryService.class, "historyQuery"));
                        result.get(clusterId).add(segmentKey.getUri());
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
                                                                          Map<String, List<SegmentDestination>> destinations) {
            Set<String> historyNodes = exists.keySet();
            Map<String, Map<String, AtomicInteger>> result = new HashMap<String, Map<String, AtomicInteger>>();
            int lessCount = historyNodes.size() - 1;
            lessCount = lessCount < 1 ? 1 : lessCount;
            Map<SegmentKey, Integer> count = new HashMap<SegmentKey, Integer>();
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
                        destinations.put(sourceKey, new ArrayList<SegmentDestination>());
                    }
                    destinations.get(sourceKey).add(new SegmentDestinationImpl(historyNode, segmentKey.getUri(), segmentKey.getOrder(), HistoryService.class, "historyQuery"));
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
            return result;
        }

        private List<Pair> sort(Map<String, AtomicInteger> readyToSort) {
            Iterator<Map.Entry<String, AtomicInteger>> iterator = readyToSort.entrySet().iterator();
            List<Pair> keys = new ArrayList<Pair>();
            while (iterator.hasNext()) {
                Map.Entry<String, AtomicInteger> entry = iterator.next();
                keys.add(new Pair(entry.getKey(), entry.getValue().get()));
            }
            Collections.sort(keys);
            return keys;
        }

        class Pair implements Comparable<Pair> {

            private String clusterId;
            private int count;

            public Pair(String clusterId, int count) {
                this.clusterId = clusterId;
                this.count = count;
            }

            @Override
            public int compareTo(Pair o) {
                return count - o.count;
            }
        }
    };

    /**
     * 计算每个节点应该load哪些segment
     * @param exists
     * @param needLoad
     * @return
     */
    Map<String, Set<URI>> calculate(Map<String, List<SegmentKey>> exists, Map<String, List<SegmentKey>> needLoad,
                                    Map<String, List<SegmentDestination>> destinations);
}
