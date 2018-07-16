package com.fr.swift.service.handler.history.rule;

import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.impl.SegmentDestinationImpl;
import com.fr.swift.service.HistoryService;

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
        public Map<String, Set<SegmentKey>> calculate(Set<String> nodeIds, Map<String, List<SegmentKey>> needLoad,
                                                      Map<String, List<SegmentDestination>> destinations) {

            int lessCount = nodeIds.size() - 1;

            Map<String, AtomicInteger> readyToSort = new HashMap<String, AtomicInteger>();

            for (String nodeId : nodeIds) {
                readyToSort.put(nodeId, new AtomicInteger(0));
            }

            // 如果历史节点只有1个，数据至少存在一份，否则至少存在节点数-1份,
            lessCount = lessCount < 1 ? 1 : lessCount;
            Map<String, Set<SegmentKey>> result = new HashMap<String, Set<SegmentKey>>();

            Set<Map.Entry<String, List<SegmentKey>>> entries = needLoad.entrySet();
            for (Map.Entry<String, List<SegmentKey>> entry : entries) {
                String sourceKey = entry.getKey();
                if (null == destinations.get(sourceKey)) {
                    destinations.put(sourceKey, new ArrayList<SegmentDestination>());
                }
                List<SegmentKey> segmentKeys = entry.getValue();
                for (SegmentKey segmentKey : segmentKeys) {
                    for (int i = 0; i < lessCount; i++) {
                        SegmentPair pair = sort(readyToSort);
                        if (null == result.get(pair.clusterId)) {
                            result.put(pair.clusterId, new HashSet<SegmentKey>());
                        }
                        result.get(pair.clusterId).add(segmentKey);
                        readyToSort.get(pair.clusterId).incrementAndGet();
                        destinations.get(sourceKey).add(new SegmentDestinationImpl(pair.clusterId,
                                segmentKey.getUri(), segmentKey.getOrder(), HistoryService.class, "historyQuery"));
                    }
                }
            }

            return result;
        }

        private SegmentPair sort(Map<String, AtomicInteger> readyToSort) {
            if (readyToSort.isEmpty()) {
                throw new RuntimeException("NodeSortMap is empty");
            }
            Iterator<Map.Entry<String, AtomicInteger>> iterator = readyToSort.entrySet().iterator();
            List<SegmentPair> keys = new ArrayList<SegmentPair>();
            while (iterator.hasNext()) {
                Map.Entry<String, AtomicInteger> entry = iterator.next();
                keys.add(new SegmentPair(entry.getKey(), entry.getValue().get()));
            }
            Collections.sort(keys);
            return keys.get(0);
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
     * @param nodeIds
     * @param needLoad
     * @return
     */
    Map<String, Set<SegmentKey>> calculate(Set<String> nodeIds, Map<String, List<SegmentKey>> needLoad,
                                           Map<String, List<SegmentDestination>> destinations);
}
