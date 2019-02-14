package com.fr.swift.service.manager;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.service.SwiftService;
import com.fr.swift.service.listener.SwiftServiceListenerManager;

import java.util.List;

/**
 * This class created on 2018/8/7
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@SwiftBean(name = "localServiceManager")
public class LocalServiceManager extends AbstractServiceManager<SwiftService> {

    private LocalServiceManager() {
    }

    @Override
    public void registerService(List<SwiftService> swiftServiceList) throws SwiftServiceException {
        lock.lock();
        try {
            for (SwiftService swiftService : swiftServiceList) {
                SwiftLoggers.getLogger().debug("begin to register " + swiftService.getServiceType() + "!");
                SwiftServiceListenerManager.getInstance().registerService(swiftService);
                SwiftLoggers.getLogger().debug("register " + swiftService.getServiceType() + " to local succeed!");
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void unregisterService(List<SwiftService> swiftServiceList) throws SwiftServiceException {
        lock.lock();
        try {
            for (SwiftService swiftService : swiftServiceList) {
                SwiftLoggers.getLogger().debug("begain to unregister " + swiftService.getServiceType() + "!");
                SwiftServiceListenerManager.getInstance().unRegisterService(swiftService);
                SwiftLoggers.getLogger().debug("unregister " + swiftService.getServiceType() + " from local succeed!");
            }
        } finally {
            lock.unlock();
        }
    }
}
