package com.fr.swift.cloud.basic;

import com.fr.swift.cloud.basics.ProcessHandler;
import com.fr.swift.cloud.basics.base.ProxyProcessHandlerRegistry;
import com.fr.swift.cloud.local.TestInvokerProcessHandler;
import junit.framework.TestCase;

/**
 * This class created on 2019/1/4
 *
 * @author Lucifer
 * @description
 */
public class ProxyProcessHandlerRegistryTest extends TestCase {

    public void testProxyProcessHandlerRegistry() {
        assertNotNull(ProxyProcessHandlerRegistry.get());
        ProxyProcessHandlerRegistry.get().addHandler(ProcessHandler.class, TestInvokerProcessHandler.class);
        assertEquals(ProxyProcessHandlerRegistry.get().getHandler(ProcessHandler.class), TestInvokerProcessHandler.class);
    }
}
