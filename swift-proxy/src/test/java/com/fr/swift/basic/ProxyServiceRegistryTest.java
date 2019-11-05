package com.fr.swift.basic;

import com.fr.swift.basic.service.TestExternalService;
import com.fr.swift.basic.service.TestInternalService;
import com.fr.swift.basics.base.ProxyServiceRegistry;
import junit.framework.TestCase;

/**
 * This class created on 2019/1/4
 *
 * @author Lucifer
 * @description
 */
public class ProxyServiceRegistryTest extends TestCase {

    public void testRegistry() {
        assertNotNull(ProxyServiceRegistry.get());
        TestInternalService internalService = new TestInternalService();
        TestExternalService externalService = new TestExternalService();
        ProxyServiceRegistry.get().registerService(internalService);
        ProxyServiceRegistry.get().registerService(externalService);

        assertEquals(ProxyServiceRegistry.get().getInternalService(TestInternalService.class), internalService);
        assertEquals(ProxyServiceRegistry.get().getInternalService(TestInternalService.class.getName()), internalService);

        assertEquals(ProxyServiceRegistry.get().getExternalService(TestExternalService.class), externalService);
        assertEquals(ProxyServiceRegistry.get().getExternalService(TestExternalService.class.getName()), externalService);

        assertEquals(ProxyServiceRegistry.get().getService(TestInternalService.class.getName()), internalService);
        assertEquals(ProxyServiceRegistry.get().getService(TestExternalService.class.getName()), externalService);

    }
}
