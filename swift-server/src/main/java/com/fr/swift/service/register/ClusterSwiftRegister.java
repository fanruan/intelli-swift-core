package com.fr.swift.service.register;

import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.rpc.SwiftRpcService;
import com.fr.swift.rpc.server.RpcServer;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.ClusterSwiftServerService;

/**
 * This class created on 2018/6/4
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ClusterSwiftRegister extends AbstractSwiftRegister {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(RpcServer.class);

    @Override
    public void serviceRegister() {
        SwiftRpcService.getInstance().startServerService();

        if (ClusterSelector.getInstance().getFactory().isMaster()) {
            new ClusterSwiftServerService().start();
            masterLocalServiceRegister();
        } else {
            remoteServiceRegister();
        }
    }

    @Override
    public void serviceUnregister() {
        try {
            SwiftRpcService.getInstance().stopServerService();
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }
}
