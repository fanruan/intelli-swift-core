package com.fr.swift.service.local;

import com.fr.swift.beans.annotation.SwiftAutoWired;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.service.AbstractSwiftManager;
import com.fr.swift.service.SwiftManager;
import com.fr.swift.service.manager.ServerServiceManager;
import com.fr.swift.util.ServiceBeanFactory;

/**
 * This class created on 2018/8/21
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@SwiftBean(name = "serverManager")
public class ServerManager extends AbstractSwiftManager implements SwiftManager {

    @SwiftAutoWired
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
