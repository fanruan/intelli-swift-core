package com.fr.swift.boot;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.base.JdkProxyFactory;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.boot.synchronizer.MasterSynchronizer;
import com.fr.swift.cluster.core.cluster.FRClusterNodeManager;
import com.fr.swift.core.rpc.FRInvokerCreator;
import com.fr.swift.core.rpc.FRUrlFactory;
import com.fr.swift.event.ClusterEvent;
import com.fr.swift.event.ClusterEventListener;
import com.fr.swift.event.ClusterEventType;
import com.fr.swift.local.LocalInvokerCreator;
import com.fr.swift.local.LocalUrlFactory;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.nm.SlaveManager;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.rm.MasterManager;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.local.LocalManager;

/**
 * This class created on 2018/6/12
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class FRClusterListener implements ClusterEventListener {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger();

    private MasterManager masterManager;

    private SlaveManager slaveManager;

    private LocalManager localManager;

    private MasterSynchronizer masterSyncRunnable = MasterSynchronizer.getInstance();

    public FRClusterListener() {
    }

    private void initIfNeed() {
        if (null == masterManager) {
            masterManager = SwiftContext.get().getBean(MasterManager.class);
        }
        if (null == slaveManager) {
            slaveManager = SwiftContext.get().getBean(SlaveManager.class);
        }
        if (null == localManager) {
            localManager = SwiftContext.get().getBean(LocalManager.class);
        }
    }

    public void handleEvent(ClusterEvent clusterEvent) {
        initIfNeed();
        try {
            if (clusterEvent.getEventType() == ClusterEventType.JOIN_CLUSTER) {
                SwiftProperty.getProperty().setCluster(true);
                ProxySelector.getInstance().switchFactory(new JdkProxyFactory(new FRInvokerCreator()));
                UrlSelector.getInstance().switchFactory(new FRUrlFactory());
                ClusterSelector.getInstance().switchFactory(FRClusterNodeManager.getInstance());
                SwiftProperty.getProperty().setClusterId(ClusterSelector.getInstance().getFactory().getCurrentId());

                localManager.shutDown();
                if (ClusterSelector.getInstance().getFactory().isMaster()) {
                    LOGGER.info("=====FR cluster master start up!=====");
                    masterManager.startUp();
                    masterSyncRunnable.start();
                } else {
                    LOGGER.info("=====FR cluster slaver start up!=====");
                    slaveManager.startUp();
                    masterSyncRunnable.stop();
                }

            } else if (clusterEvent.getEventType() == ClusterEventType.LEFT_CLUSTER) {
                SwiftProperty.getProperty().setCluster(false);
                ProxySelector.getInstance().switchFactory(new JdkProxyFactory(new LocalInvokerCreator()));
                UrlSelector.getInstance().switchFactory(new LocalUrlFactory());

                masterManager.shutDown();
                slaveManager.shutDown();
                masterSyncRunnable.stop();
                localManager.startUp();
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }
}
