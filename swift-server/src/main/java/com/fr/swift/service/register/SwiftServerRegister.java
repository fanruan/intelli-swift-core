package com.fr.swift.service.register;

import com.fr.swift.service.manager.ServerServiceManager;
import com.fr.swift.util.ServiceBeanFactory;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

/**
 * This class created on 2018/8/7
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Service("swiftServerRegister")
public class SwiftServerRegister extends AbstractSwiftRegister {

    @Autowired
    private ServerServiceManager serverServiceManager;

    @Override
    public void serviceRegister() throws Exception {
        serverServiceManager.registerService(ServiceBeanFactory.getServerServiceByNames(swiftProperty.getServerServiceNames()));
    }

    @Override
    public void serviceUnregister() throws Exception {
        serverServiceManager.unregisterService(ServiceBeanFactory.getServerServiceByNames(swiftProperty.getServerServiceNames()));
    }
}
