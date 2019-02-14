package com.fr.swift.rm;

import com.fr.swift.ClusterNodeService;
import com.fr.swift.Collect;
import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.rm.collector.MasterHeartbeatCollect;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.AbstractSwiftManager;
import com.fr.swift.service.SwiftManager;
import com.fr.swift.service.SwiftService;
import com.fr.swift.service.executor.CollateExecutor;
import com.fr.swift.service.listener.SwiftServiceListenerManager;
import com.fr.swift.service.local.ServiceManager;
import com.fr.swift.util.ServiceBeanFactory;

import java.util.List;

/**
 * This class created on 2018/7/17
 *
 * @author Lucifer
 * @description 控制集群情况下，service启动和service register和unregister，master只会向本地注册和注销。
 * @since Advanced FineBI 5.0
 */
@SwiftBean(name = "masterManager")
public class MasterManager extends AbstractSwiftManager implements SwiftManager {

    private ServiceManager serviceManager = SwiftContext.get().getBean(ServiceManager.class);

    private SwiftServiceInfoService serviceInfoService = SwiftContext.get().getBean(SwiftServiceInfoService.class);

    private CollateExecutor collateExecutor = SwiftContext.get().getBean(CollateExecutor.class);

    private Collect heartBeatCollect = new MasterHeartbeatCollect();

    @Override
    public void startUp() throws Exception {
        lock.lock();
        try {
            if (!running) {
                heartBeatCollect.startCollect();
                collateExecutor.start();
                super.startUp();
                String masterAddress = swiftProperty.getMasterAddress();
                serviceInfoService.saveOrUpdate(new SwiftServiceInfoBean(ClusterNodeService.SERVICE, masterAddress, masterAddress, true));
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void shutDown() throws Exception {
        lock.lock();
        try {
            if (running) {
                heartBeatCollect.stopCollect();
                collateExecutor.stop();
                super.shutDown();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected void installService() {
        try {
            ClusterSwiftServerService.getInstance().start();
            serviceManager.startUp();
            List<SwiftService> swiftServices = ServiceBeanFactory.getSwiftServiceByNames(swiftProperty.getSwiftServiceNames());
            for (SwiftService swiftService : swiftServices) {
                swiftService.setId(ClusterSelector.getInstance().getFactory().getCurrentId());
                SwiftServiceListenerManager.getInstance().registerService(swiftService);
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    @Override
    protected void uninstallService() {
        try {
            List<SwiftService> swiftServices = ServiceBeanFactory.getSwiftServiceByNames(swiftProperty.getSwiftServiceNames());
            for (SwiftService swiftService : swiftServices) {
                swiftService.setId(ClusterSelector.getInstance().getFactory().getCurrentId());
                SwiftServiceListenerManager.getInstance().unRegisterService(swiftService);
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }
}
