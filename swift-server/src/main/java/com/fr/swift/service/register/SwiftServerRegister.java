package com.fr.swift.service.register;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.service.SwiftRegister;
import com.fr.swift.service.manager.ServiceManager;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.beans.factory.annotation.Value;
import com.fr.third.springframework.stereotype.Service;

/**
 * This class created on 2018/8/7
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Service("swiftServerRegister")
public class SwiftServerRegister implements SwiftRegister {

    @Autowired
    public void setServerServiceList(@Value("${server.service.name}") String serverServiceName) {
        SwiftProperty swiftProperty = SwiftContext.get().getBean(SwiftProperty.class);
        String serverServiceNames[] = serverServiceName.split(",");
        swiftProperty.setServerServiceList(serverServiceNames);
    }

    @Override
    public void serviceRegister() throws Exception {
        ServiceManager serviceManager = SwiftContext.get().getBean("serverServiceManager", ServiceManager.class);
        SwiftProperty swiftProperty = SwiftContext.get().getBean(SwiftProperty.class);
        serviceManager.registerService(swiftProperty.getServerServiceList());
    }

    @Override
    public void serviceUnregister() throws Exception {
        ServiceManager serviceManager = SwiftContext.get().getBean("serverServiceManager", ServiceManager.class);
        SwiftProperty swiftProperty = SwiftContext.get().getBean(SwiftProperty.class);
        serviceManager.unregisterService(swiftProperty.getServerServiceList());
    }
}
