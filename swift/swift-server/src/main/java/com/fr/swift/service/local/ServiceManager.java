package com.fr.swift.service.local;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.service.AbstractSwiftManager;
import com.fr.swift.service.LocalSwiftServerService;
import com.fr.swift.service.manager.LocalServiceManager;
import com.fr.swift.util.ServiceBeanFactory;

/**
 * This class created on 2018/8/21
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@SwiftBean(name = "localManager")
public class ServiceManager extends AbstractSwiftManager implements LocalManager {

    private LocalServiceManager localServiceManager = SwiftContext.get().getBean(LocalServiceManager.class);

    @Override
    public void startUp() throws Exception {
        lock.lock();
        try {
            if (!running) {
                super.startUp();
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
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected void installService() throws Exception {
        new LocalSwiftServerService().start();
        localServiceManager.registerService(ServiceBeanFactory.getSwiftServiceByNames(swiftProperty.getSwiftServiceNames()));
    }

    @Override
    protected void uninstallService() throws Exception {
        localServiceManager.unregisterService(ServiceBeanFactory.getSwiftServiceByNames(swiftProperty.getSwiftServiceNames()));
    }
}
