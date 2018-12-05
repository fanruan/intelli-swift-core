package com.fr.swift.config.convert;

import com.fr.swift.config.DataSyncRule;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.source.SourceKey;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yee
 * @date 2018-11-29
 */
public class TestDataSyncRule implements DataSyncRule {
    private String name;

    public TestDataSyncRule() {
    }

    public TestDataSyncRule(String name) {
        this.name = name;
    }

    @Override
    public Map<String, Set<SegmentKey>> getNeedLoadAndUpdateDestinations(Set<String> nodeIds, Set<SegmentKey> needLoads, Map<SourceKey, List<SegmentDestination>> destinations) {
        return Collections.emptyMap();
    }

    @Override
    public String toString() {
        return name;
    }
}
