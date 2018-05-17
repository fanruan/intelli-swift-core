package com.fr.swift.cluster;

import com.fr.cluster.core.ClusterNode;
import com.fr.cluster.core.event.ClusterViewEvent;
import com.fr.cluster.entry.ClusterTicketAdaptor;
import com.fr.cluster.entry.ClusterToolKit;
import com.fr.event.Event;
import com.fr.event.EventDispatcher;
import com.fr.event.Listener;
import com.fr.swift.adaptor.log.LogOperatorProxy;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;

/**
 * This class created on 2018/5/14
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftClusterTicket extends ClusterTicketAdaptor {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(ClusterCompete.class);

    private static final SwiftClusterTicket INSTANCE = new SwiftClusterTicket();

    private SwiftClusterTicket() {
    }

    public static SwiftClusterTicket getInstance() {
        return INSTANCE;
    }

    @Override
    public void beforeJoin() {

    }

    @Override
    public void approach(ClusterToolKit clusterToolKit) {
        ((LogOperatorProxy) LogOperatorProxy.getInstance()).switchCluster();
        EventDispatcher.listen(ClusterViewEvent.NODE_LEFT, new NodeLeftListener());
    }

    @Override
    public void catchUpWith(ClusterNode clusterNode) {

    }

    @Override
    public void afterJoin() {
        ClusterCompete.getInstance().competeMaster();
    }

    @Override
    public void onLeft() {
        ((LogOperatorProxy) LogOperatorProxy.getInstance()).switchSingle();
    }

    private class NodeLeftListener extends Listener {
        @Override
        public void on(Event event, Object o) {
            ClusterCompete.getInstance().competeMaster();
        }
    }

}
