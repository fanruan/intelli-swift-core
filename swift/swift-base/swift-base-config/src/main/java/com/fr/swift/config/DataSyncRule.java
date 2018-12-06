package com.fr.swift.config;

import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.source.SourceKey;

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
     * @param nodeIds 需要load的节点id
     * @param needLoads 需要load的segmentKey
     * @param destinations 现有的分布
     * @return
     */
    Map<String, Set<SegmentKey>> getNeedLoadAndUpdateDestinations(Set<String> nodeIds, Set<SegmentKey> needLoads,
                                                                  Map<SourceKey, List<SegmentDestination>> destinations);
}
