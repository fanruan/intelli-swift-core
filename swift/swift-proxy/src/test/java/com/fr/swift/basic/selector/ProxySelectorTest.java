package com.fr.swift.basic.selector;

import com.fr.swift.basics.ProcessHandler;
import com.fr.swift.basics.ProxyFactory;
import com.fr.swift.basics.base.JdkProxyFactory;
import com.fr.swift.basics.base.selector.ProxySelector;
import junit.framework.TestCase;
import org.easymock.EasyMock;

import java.lang.reflect.Proxy;

/**
 * This class created on 2019/1/4
 *
 * @author Lucifer
 * @description
 */
public class ProxySelectorTest extends TestCase {
    public void testProxySelector() {

        assertNotNull(ProxySelector.getInstance().getFactory());
        assertTrue(ProxySelector.getInstance().getFactory() instanceof JdkProxyFactory);

        ProcessHandler proxy = ProxySelector.getInstance().getProxy(ProcessHandler.class);
        assertTrue(proxy instanceof Proxy);
        assertTrue(proxy instanceof ProcessHandler);
        ProxyFactory proxyFactory = EasyMock.createMock(ProxyFactory.class);
        ProxySelector.getInstance().switchFactory(proxyFactory);
        assertFalse(ProxySelector.getInstance().getFactory() instanceof JdkProxyFactory);
    }
}
