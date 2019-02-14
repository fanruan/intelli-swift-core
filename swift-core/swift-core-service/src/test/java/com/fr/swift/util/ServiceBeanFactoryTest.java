package com.fr.swift.util;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.service.ServerService;
import com.fr.swift.service.SwiftService;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class created on 2019/1/4
 *
 * @author Lucifer
 * @description
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SwiftContext.class, ServiceBeanFactory.class})
public class ServiceBeanFactoryTest extends TestCase {
    @Override
    public void setUp() throws Exception {

        SwiftService indexingService = new TestIndexingService();
        SwiftService collateService = new TestCollateService();
        Map<String, Object> swiftServiceMap = new HashMap<String, Object>();
        swiftServiceMap.put("indexing", indexingService);
        swiftServiceMap.put("collate", collateService);

        ServerService serverService = new TestServerService();
        Map<String, Object> serverServiceMap = new HashMap<String, Object>();
        serverServiceMap.put("rpc", serverService);

        PowerMock.mockStatic(SwiftContext.class);
        BeanFactory beanFactory = EasyMock.createMock(BeanFactory.class);
        EasyMock.expect(SwiftContext.get()).andReturn(beanFactory).anyTimes();

        EasyMock.expect(beanFactory.getBeansByAnnotations(com.fr.swift.annotation.SwiftService.class)).andReturn(swiftServiceMap).anyTimes();
        EasyMock.expect(beanFactory.getBeansByAnnotations(com.fr.swift.annotation.ServerService.class)).andReturn(serverServiceMap).anyTimes();
        EasyMock.expect(beanFactory.getBean("indexing")).andReturn(indexingService).anyTimes();
        EasyMock.expect(beanFactory.getBean("collate")).andReturn(collateService).anyTimes();
        EasyMock.expect(beanFactory.getBean("rpc", ServerService.class)).andReturn(serverService).anyTimes();
        PowerMock.replay(SwiftContext.class);
        EasyMock.replay(beanFactory);
    }

    public void testTestServiceBeanFactory() {
        assertEquals(ServiceBeanFactory.getAllSwiftServiceNames().size(), 2);
        assertEquals(ServiceBeanFactory.getAllServerServiceNames().size(), 1);

        Set<String> swiftServiceNames = new HashSet<String>();
        swiftServiceNames.add("indexing");
        assertEquals(ServiceBeanFactory.getSwiftServiceByNames(swiftServiceNames).size(), 2);
        assertEquals(ServiceBeanFactory.getServerServiceByNames(Collections.singleton("rpc")).size(), 1);
    }
}
