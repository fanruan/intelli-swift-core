package com.fr.swift.service.register;

import com.fr.swift.service.LocalSwiftServerService;
import com.fr.swift.service.manager.LocalServiceManager;
import com.fr.swift.util.ServiceBeanFactory;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

/**
 * This class created on 2018/6/1
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Service("localSwiftRegister")
public class LocalSwiftRegister extends AbstractSwiftRegister {

    @Autowired
    private LocalServiceManager localServiceManager;

    private LocalSwiftRegister() {
    }

    @Override
    public void serviceRegister() throws Exception {
        new LocalSwiftServerService().start();
        localServiceManager.registerService(ServiceBeanFactory.getSwiftServiceByNames(swiftProperty.getSwiftServiceNames()));
    }

    @Override
    public void serviceUnregister() throws Exception {
        localServiceManager.unregisterService(ServiceBeanFactory.getSwiftServiceByNames(swiftProperty.getSwiftServiceNames()));
    }
}
