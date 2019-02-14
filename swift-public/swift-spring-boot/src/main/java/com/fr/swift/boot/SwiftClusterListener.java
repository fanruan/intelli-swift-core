package com.fr.swift.boot;

import com.fr.swift.SwiftContext;
import com.fr.swift.annotation.ClusterService;
import com.fr.swift.basics.base.JdkProxyFactory;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.event.ClusterEvent;
import com.fr.swift.event.ClusterEventListener;
import com.fr.swift.event.ClusterEventType;
import com.fr.swift.local.LocalInvokerCreator;
import com.fr.swift.local.LocalUrlFactory;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.netty.rpc.invoke.RPCInvokerCreator;
import com.fr.swift.netty.rpc.url.RPCUrlFactory;
import com.fr.swift.nm.SlaveManager;
import com.fr.swift.node.SwiftClusterNodeManager;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.rm.MasterManager;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.local.LocalManager;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * This class created on 2018/7/18
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftClusterListener implements ClusterEventListener {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger();

    private MasterManager masterManager;

    private SlaveManager slaveManager;

    private LocalManager localManager;

    public SwiftClusterListener() {
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

    @Override
    public void handleEvent(ClusterEvent clusterEvent) {
        initIfNeed();
        try {
            if (clusterEvent.getEventType() == ClusterEventType.JOIN_CLUSTER) {
                SwiftProperty.getProperty().setCluster(true);
                ProxySelector.getInstance().switchFactory(new JdkProxyFactory(new RPCInvokerCreator()));
                UrlSelector.getInstance().switchFactory(new RPCUrlFactory());
                ClusterSelector.getInstance().switchFactory(SwiftClusterNodeManager.getInstance());

                initClusterPluginService();

                localManager.shutDown();
                if (ClusterSelector.getInstance().getFactory().isMaster()) {
                    LOGGER.info("=====Swift cluster master start up!=====");
                    masterManager.startUp();
                } else {
                    LOGGER.info("=====Swift cluster slaver start up!=====");
                    slaveManager.startUp();
                }
            } else if (clusterEvent.getEventType() == ClusterEventType.LEFT_CLUSTER) {
                SwiftProperty.getProperty().setCluster(false);
                ProxySelector.getInstance().switchFactory(new JdkProxyFactory(new LocalInvokerCreator()));
                UrlSelector.getInstance().switchFactory(new LocalUrlFactory());

                destroyClusterPluginService();

                masterManager.shutDown();
                slaveManager.shutDown();
                localManager.startUp();
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    /**
     * 加载init集群插件service（zk选举service）
     */
    private static void initClusterPluginService() {
        Map<String, Object> map = SwiftContext.get().getBeansByAnnotations(ClusterService.class);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            try {
                String initMethodName = entry.getValue().getClass().getAnnotation(ClusterService.class).initMethod();
                Method method = entry.getValue().getClass().getMethod(initMethodName);
                method.invoke(entry.getValue());
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
                continue;
            }
        }
        return;
    }

    /**
     *
     */
    private static void destroyClusterPluginService() {
        Map<String, Object> map = SwiftContext.get().getBeansByAnnotations(ClusterService.class);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            try {
                String destroyMethodName = entry.getValue().getClass().getAnnotation(ClusterService.class).destroyMethod();
                Method method = entry.getValue().getClass().getMethod(destroyMethodName);
                method.invoke(entry.getValue());
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
                continue;
            }
        }
        return;
    }
}
