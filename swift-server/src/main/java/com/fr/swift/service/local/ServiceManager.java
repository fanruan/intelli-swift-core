package com.fr.swift.service.local;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.service.AbstractSwiftManager;
import com.fr.swift.service.SwiftManager;
import com.fr.swift.service.SwiftService;
import com.fr.swift.util.ServiceBeanFactory;

import java.util.List;

/**
 * This class created on 2018/8/21
 *
 * @author Lucifer
 * @description 统一控制service的start和shutdown，并且记录running状态，避免重复start和shutdown
 * @since Advanced FineBI 5.0
 */
@SwiftBean(name = "serviceManager")
public class ServiceManager extends AbstractSwiftManager implements SwiftManager {

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
        List<SwiftService> swiftServices = ServiceBeanFactory.getSwiftServiceByNames(swiftProperty.getSwiftServiceNames());
        for (SwiftService swiftService : swiftServices) {
            swiftService.start();
        }
    }

    @Override
    protected void uninstallService() throws Exception {
        List<SwiftService> swiftServices = ServiceBeanFactory.getSwiftServiceByNames(swiftProperty.getSwiftServiceNames());
        for (SwiftService swiftService : swiftServices) {
            swiftService.shutdown();
        }
    }
}
