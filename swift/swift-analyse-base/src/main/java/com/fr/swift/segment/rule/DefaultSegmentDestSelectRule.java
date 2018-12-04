package com.fr.swift.segment.rule;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.SegmentDestSelectRule;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.impl.RealTimeSegDestImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/7/16
 */
@SwiftBean(name = "defaultSegmentDestSelectRule")
public class DefaultSegmentDestSelectRule implements SegmentDestSelectRule {
    @Override
    public List<SegmentDestination> selectDestination(List<SegmentDestination> duplicate) {
        Map<String, SegmentDestination> destinationMap = new HashMap<String, SegmentDestination>();
        for (SegmentDestination destination : duplicate) {
            if (destinationMap.containsKey(destination.getSegmentId())) {
                if (destination instanceof RealTimeSegDestImpl && destination.isRemote()) {
                    String key = destination.getSegmentId() + destination.getClusterId();
                    if (!destinationMap.containsKey(key)) {
                        destinationMap.put(key, destination);
                    }
                } else {
                    destinationMap.get(destination.getSegmentId()).getSpareNodes().add(destination.getClusterId());
                }
            } else {
                destinationMap.put(destination.getSegmentId(), destination);
                SwiftLoggers.getLogger().debug("Prepare segment query destination from {} -> {} segment is {}", destination.getCurrentNode(), destination.getClusterId(), destination.getSegmentId());
            }
        }
        List<SegmentDestination> list = new ArrayList<SegmentDestination>(destinationMap.values());
        Collections.sort(list);
        return Collections.unmodifiableList(list);
    }
}
