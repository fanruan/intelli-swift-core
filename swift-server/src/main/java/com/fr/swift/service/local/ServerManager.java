package com.fr.swift.service.local;

import com.fr.swift.service.AbstractSwiftManager;
import com.fr.swift.service.manager.ServerServiceManager;
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
@Service("serverManager")
public class ServerManager extends AbstractSwiftManager implements LocalManager {

    @Autowired
    private ServerServiceManager serverServiceManager;

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
        serverServiceManager.registerService(ServiceBeanFactory.getServerServiceByNames(swiftProperty.getServerServiceNames()));
    }

    @Override
    protected void uninstallService() throws Exception {
        serverServiceManager.unregisterService(ServiceBeanFactory.getServerServiceByNames(swiftProperty.getServerServiceNames()));
    }
}
