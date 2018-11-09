package com.fr.swift.service.handler.history.rule;

import com.fr.swift.config.bean.DataSyncRule;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.impl.SegmentDestinationImpl;
import com.fr.swift.service.HistoryService;
import com.fr.third.springframework.stereotype.Service;

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
 * @date 2018/7/16
 */
@Service("defaultDataSyncRule")
public class DefaultDataSyncRule implements DataSyncRule {
    @Override
    public Map<String, Set<SegmentKey>> calculate(Set<String> nodeIds, Set<SegmentKey> needLoads,
                                                  Map<String, List<SegmentDestination>> destinations) {

        int lessCount = nodeIds.size();

        Map<String, AtomicInteger> readyToSort = new HashMap<String, AtomicInteger>();

        for (String nodeId : nodeIds) {
            readyToSort.put(nodeId, new AtomicInteger(0));
        }

        // 最多存3份，最少存node份
        lessCount = lessCount > 3 ? 3 : lessCount;
        Map<String, Set<SegmentKey>> result = new HashMap<String, Set<SegmentKey>>();

        for (SegmentKey needLoad : needLoads) {
            String sourceKey = needLoad.getTable().getId();
            if (null == destinations.get(sourceKey)) {
                destinations.put(sourceKey, new ArrayList<SegmentDestination>());
            }

            for (int i = 0; i < lessCount; i++) {
                SegmentPair pair = sort(readyToSort);
                if (null == result.get(pair.getClusterId())) {
                    result.put(pair.getClusterId(), new HashSet<SegmentKey>());
                }
                result.get(pair.getClusterId()).add(needLoad);
                readyToSort.get(pair.getClusterId()).incrementAndGet();
                destinations.get(sourceKey).add(new SegmentDestinationImpl(pair.getClusterId(),
                        needLoad.toString(), needLoad.getOrder(), HistoryService.class, "historyQuery"));
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

}
