package com.fr.swift.segment.rule;

import com.fr.swift.config.bean.SegmentDestSelectRule;
import com.fr.swift.segment.SegmentDestination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/7/16
 */
public class DefaultSegmentDestSelectRule implements SegmentDestSelectRule {
    @Override
    public List<SegmentDestination> selectDestination(List<SegmentDestination> duplicate) {
        Map<String, SegmentDestination> destinationMap = new HashMap<String, SegmentDestination>();
        for (SegmentDestination destination : duplicate) {
            if (destinationMap.containsKey(destination.getSegmentId())) {
                destinationMap.get(destination.getSegmentId()).getSpareNodes().add(destination.getClusterId());
            } else {
                destinationMap.put(destination.getSegmentId(), destination);
            }
        }
        List<SegmentDestination> list = new ArrayList<SegmentDestination>(destinationMap.values());
        Collections.sort(list);
        return Collections.unmodifiableList(list);
    }
}
