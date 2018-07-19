package com.fr.swift.boot;

import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.core.cluster.FRClusterNodeManager;
import com.fr.swift.core.rpc.FRProxyFactory;
import com.fr.swift.core.rpc.FRUrlFactory;
import com.fr.swift.event.ClusterEvent;
import com.fr.swift.event.ClusterEventListener;
import com.fr.swift.event.ClusterEventType;
import com.fr.swift.local.LocalProxyFactory;
import com.fr.swift.local.LocalUrlFactory;
import com.fr.swift.selector.ClusterSelector;

/**
 * This class created on 2018/6/12
 *
 * @author Lucifer
 * @description todo fr平台的集群先简化处理
 * @since Advanced FineBI 5.0
 */
public class FRClusterListener implements ClusterEventListener {
    public void handleEvent(ClusterEvent clusterEvent) {
        if (clusterEvent.getEventType() == ClusterEventType.JOIN_CLUSTER) {
            ProxySelector.getInstance().switchFactory(new FRProxyFactory());
            UrlSelector.getInstance().switchFactory(new FRUrlFactory());
            ClusterSelector.getInstance().switchFactory(FRClusterNodeManager.getInstance());
        } else if (clusterEvent.getEventType() == ClusterEventType.LEFT_CLUSTER) {
            ProxySelector.getInstance().switchFactory(new LocalProxyFactory());
            UrlSelector.getInstance().switchFactory(new LocalUrlFactory());
        }
    }
}
