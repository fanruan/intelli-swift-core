package com.fr.swift.service.local;

import com.fr.swift.service.AbstractSwiftManager;
import com.fr.swift.service.LocalSwiftServerService;
import com.fr.swift.service.manager.LocalServiceManager;
import com.fr.swift.util.ServiceBeanFactory;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

/**
 * This class created on 2018/8/21
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Service("localManager")
public class ServiceManager extends AbstractSwiftManager implements LocalManager {

    @Autowired
    private LocalServiceManager localServiceManager;

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
