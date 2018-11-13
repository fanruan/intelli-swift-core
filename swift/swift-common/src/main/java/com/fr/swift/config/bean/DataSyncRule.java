package com.fr.swift.config.bean;

import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentKey;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yee
 * @date 2018/6/11
 */
public interface DataSyncRule {
    /**
     * 计算每个节点应该load哪些segment
     *
     * @param nodeIds
     * @param needLoads
     * @return
     */
    Map<String, Set<SegmentKey>> calculate(Set<String> nodeIds, Set<SegmentKey> needLoads,
                                           Map<String, List<SegmentDestination>> destinations);
}
