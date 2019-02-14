package com.fr.swift.service.local;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.service.AbstractSwiftManager;
import com.fr.swift.service.LocalSwiftServerService;
import com.fr.swift.service.SwiftManager;
import com.fr.swift.service.SwiftService;
import com.fr.swift.service.executor.CollateExecutor;
import com.fr.swift.service.manager.LocalServiceManager;
import com.fr.swift.util.ServiceBeanFactory;

import java.util.List;

/**
 * This class created on 2019/1/11
 *
 * @author Lucifer
 * @description 控制单机情况下，service启动和service register和unregister
 */
@SwiftBean(name = "localManager")
public class LocalManager extends AbstractSwiftManager implements SwiftManager {

    private ServiceManager serviceManager = SwiftContext.get().getBean(ServiceManager.class);

    private LocalServiceManager localServiceManager = SwiftContext.get().getBean(LocalServiceManager.class);

    private CollateExecutor collateExecutor = SwiftContext.get().getBean(CollateExecutor.class);

    @Override
    public void startUp() throws Exception {
        lock.lock();
        try {
            if (!running) {
                super.startUp();
                collateExecutor.start();
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
                super.shutDown();
                collateExecutor.stop();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected void installService() throws Exception {
        new LocalSwiftServerService().start();
        serviceManager.startUp();
        List<SwiftService> swiftServices = ServiceBeanFactory.getSwiftServiceByNames(swiftProperty.getSwiftServiceNames());
        localServiceManager.registerService(swiftServices);
    }

    @Override
    protected void uninstallService() throws Exception {
        List<SwiftService> swiftServices = ServiceBeanFactory.getSwiftServiceByNames(swiftProperty.getSwiftServiceNames());
        localServiceManager.unregisterService(swiftServices);
    }
}
