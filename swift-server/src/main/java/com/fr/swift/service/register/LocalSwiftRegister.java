package com.fr.swift.service.register;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.service.LocalSwiftServerService;
import com.fr.swift.service.manager.ServiceManager;
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

    private LocalSwiftRegister() {
    }

    @Override
    public void serviceRegister() throws Exception {
        new LocalSwiftServerService().start();
        ServiceManager serviceManager = SwiftContext.get().getBean("localServiceManager", ServiceManager.class);
        SwiftProperty swiftProperty = SwiftContext.get().getBean(SwiftProperty.class);
        serviceManager.registerService(swiftProperty.getSwiftServiceList());
    }

    @Override
    public void serviceUnregister() throws Exception {
        ServiceManager serviceManager = SwiftContext.get().getBean("localServiceManager", ServiceManager.class);
        SwiftProperty swiftProperty = SwiftContext.get().getBean(SwiftProperty.class);
        serviceManager.unregisterService(swiftProperty.getSwiftServiceList());
    }
}
