package com.fr.swift.service.manager;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.service.ServerService;

import java.util.List;

/**
 * This class created on 2018/8/7
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@SwiftBean(name = "serverServiceManager")
public class ServerServiceManager extends AbstractServiceManager<ServerService> {

    @Override
    public void registerService(List<ServerService> serverServiceList) throws Exception {
        lock.lock();
        try {
            for (ServerService serverService : serverServiceList) {
                serverService.startServerService();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void unregisterService(List<ServerService> serverServiceList) throws Exception {
        lock.lock();
        try {
            for (ServerService serverService : serverServiceList) {
                serverService.stopServerService();
            }
        } finally {
            lock.unlock();
        }
    }
}
