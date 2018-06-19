package com.fr.swift.segment.rule;

import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.impl.SegmentDestinationImpl;
import com.fr.swift.structure.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yee
 * @date 2018/6/19
 */
public interface DestSelectRule {
    DestSelectRule DEFAULT = new DestSelectRule() {
        private final SwiftLogger LOGGER = SwiftLoggers.getLogger(DestSelectRule.class);

        @Override
        public List<SegmentDestination> selectDestination(int total, List<SegmentDestination> duplicate) {
            List<SegmentDestination> result = new ArrayList<SegmentDestination>() {
                @Override
                public boolean add(SegmentDestination segmentDestination) {
                    return !contains(segmentDestination) && super.add(segmentDestination);
                }
            };
            Map<SegmentDestination, List<String>> segmentDestinationMap = calculateCluster(duplicate);
            Map<String, AtomicInteger> count = new HashMap<String, AtomicInteger>();
            Iterator<Map.Entry<SegmentDestination, List<String>>> iterator = segmentDestinationMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<SegmentDestination, List<String>> entry = iterator.next();
                SegmentDestination destination = entry.getKey();
                List<String> clusterIds = entry.getValue();
                List<Pair<String, AtomicInteger>> sortList = new ArrayList<Pair<String, AtomicInteger>>();
                for (String clusterId : clusterIds) {
                    sortList.add(Pair.of(clusterId, count.get(clusterId)));
                }
                Collections.sort(sortList, new Comparator<Pair<String, AtomicInteger>>() {
                    @Override
                    public int compare(Pair<String, AtomicInteger> o1, Pair<String, AtomicInteger> o2) {
                        return o1.getValue().get() - o2.getValue().get();
                    }
                });
                SegmentDestinationImpl targetDestination = new SegmentDestinationImpl(destination);
                targetDestination.setClusterId(sortList.get(0).getKey());
                List<Pair<String, AtomicInteger>> sparePair = sortList.size() > 1 ? sortList.subList(1, sortList.size()) : Collections.<Pair<String, AtomicInteger>>emptyList();
                List<String> spareList = new ArrayList<String>();
                for (Pair<String, AtomicInteger> pair : sparePair) {
                    spareList.add(pair.getKey());
                }
                targetDestination.setSpareNodes(spareList);
                result.add(targetDestination);
            }
            if (result.size() != total) {
                LOGGER.warn(String.format("Destinations not match. Total %d but got %d", total, result.size()));
            }
            Collections.sort(result);
            return Collections.unmodifiableList(result);
        }

        private Map<SegmentDestination, List<String>> calculateCluster(List<SegmentDestination> duplicate) {
            Map<SegmentDestination, List<String>> result = new HashMap<SegmentDestination, List<String>>();
            for (SegmentDestination destination : duplicate) {
                if (null == result.get(destination)) {
                    result.put(destination, new ArrayList<String>() {
                        @Override
                        public boolean add(String clusterId) {
                            return !contains(clusterId) && super.add(clusterId);
                        }
                    });
                }
                result.get(destination).add(destination.getClusterId());
            }
            return result;
        }

    };

    List<SegmentDestination> selectDestination(int total, List<SegmentDestination> duplicate);
}
