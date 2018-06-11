package com.fr.swift.boot;

import com.fr.module.Activator;
import com.fr.module.extension.Prepare;
import com.fr.stable.db.constant.BaseDBConstant;
import com.fr.swift.config.context.SwiftConfigContext;
import com.fr.swift.config.entity.SwiftMetaDataEntity;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.config.entity.SwiftServiceInfoEntity;
import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.config.service.SwiftSegmentServiceProvider;
import com.fr.swift.config.service.impl.SwiftClusterSegmentServiceImpl;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.queue.ProviderTaskManager;
import com.fr.swift.event.ClusterEvent;
import com.fr.swift.event.ClusterEventListener;
import com.fr.swift.event.ClusterEventType;
import com.fr.swift.event.ClusterListenerHandler;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.frrpc.ClusterNodeManager;
import com.fr.swift.frrpc.FRClusterProxyFactory;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.proxy.LocalProxyFactory;
import com.fr.swift.selector.ProxySelector;
import com.fr.swift.service.register.ClusterSwiftRegister;
import com.fr.swift.service.register.LocalSwiftRegister;
import com.fr.swift.util.Crasher;

/**
 * @author anchore
 * @date 2018/5/24
 */
public class SwiftEngineActivator extends Activator implements Prepare {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftEngineActivator.class);

    @Override
    public void start() {
        startSwift();
    }

    private void startSwift() {
        try {
            SwiftConfigContext.getInstance().init();
            SwiftContext.init();

            new LocalSwiftRegister().serviceRegister();
            ClusterListenerHandler.addListener(new ClusterEventListener() {
                @Override
                public void handleEvent(ClusterEvent clusterEvent) {
                    if (clusterEvent.getEventType() == ClusterEventType.JOIN_CLUSTER) {
                        ProxySelector.getInstance().switchFactory(new FRClusterProxyFactory());
                        new LocalSwiftRegister().serviceUnregister();
                        new ClusterSwiftRegister().serviceRegister();
                        SwiftClusterSegmentServiceImpl service = (SwiftClusterSegmentServiceImpl) SwiftContext.getInstance().getBean(SwiftClusterSegmentService.class);
                        service.setClusterId(ClusterNodeManager.getInstance().getCurrentId());
                        SwiftSegmentServiceProvider.getProvider().setService(service);
                    } else if (clusterEvent.getEventType() == ClusterEventType.LEFT_CLUSTER) {
                        try {
                            ProxySelector.getInstance().switchFactory(new LocalProxyFactory());
                            new ClusterSwiftRegister().serviceUnregister();
                            new LocalSwiftRegister().serviceRegister();
                            SwiftSegmentServiceProvider.getProvider().setService(SwiftContext.getInstance().getBean("swiftSegmentService", SwiftSegmentService.class));
                        } catch (SwiftServiceException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            ProviderTaskManager.start();
            SwiftLoggers.getLogger().info("swift engine started");
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("swift engine start failed", e);
            Crasher.crash(e);
        }
    }

    @Override
    public void stop() {
        SwiftLoggers.getLogger().info("swift engine stopped");
    }

    @Override
    public void prepare() {
        this.addMutable(BaseDBConstant.BASE_ENTITY_KEY, SwiftMetaDataEntity.class, SwiftSegmentEntity.class, SwiftServiceInfoEntity.class);
    }
}