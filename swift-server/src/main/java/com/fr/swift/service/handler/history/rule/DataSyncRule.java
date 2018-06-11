package com.fr.swift.service.handler.history.rule;

import com.fr.swift.segment.SegmentKey;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yee
 * @date 2018/6/11
 */
public interface DataSyncRule {
    DataSyncRule DEFAULT = new DataSyncRule() {
        @Override
        public Map<String, Set<URI>> calculate(Map<String, List<SegmentKey>> exists, List<SegmentKey> needLoad) {

            Set<String> historyNodes = exists.keySet();


            return new HashMap<String, Set<URI>>();
        }
    };

    Map<String, Set<URI>> calculate(Map<String, List<SegmentKey>> exists, List<SegmentKey> needLoad);
}
