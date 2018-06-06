package com.fr.swift.service.register;

import com.fr.swift.frrpc.ClusterNodeManager;
import com.fr.swift.service.ClusterSwiftServerService;

/**
 * This class created on 2018/6/4
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ClusterSwiftRegister extends AbstractSwiftRegister {
    @Override
    public void serviceRegister() {
        if (!ClusterNodeManager.getInstance().isMaster()) {
            remoteServiceRegister();
        } else {
            new ClusterSwiftServerService().start();
            masterLocalServiceRegister();
        }
    }

    @Override
    public void serviceUnregister() {

    }
}
