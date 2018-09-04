package com.fr.swift.common;

import com.fr.swift.event.ClusterEvent;
import com.fr.swift.event.ClusterEventListener;
import com.fr.swift.event.ClusterEventType;
import com.fr.swift.event.global.NodeStartedEvent;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.utils.ClusterCommonUtils;

/**
 * @author yee
 * @date 2018/9/4
 */
public class NodeStartedListener implements ClusterEventListener {
    @Override
    public void handleEvent(ClusterEvent clusterEvent) {
        if (clusterEvent.getEventType() == ClusterEventType.JOIN_CLUSTER) {
            String currentId = ClusterSelector.getInstance().getFactory().getCurrentId();
            try {
                ClusterCommonUtils.asyncCallMaster(new NodeStartedEvent(currentId));
            } catch (Exception ignore) {
            }
        }
    }
}
