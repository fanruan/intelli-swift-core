package com.fr.swift.invoker;

import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.Result;
import com.fr.swift.basics.base.ProxyServiceRegistry;
import com.fr.swift.basics.base.SwiftInvocation;
import com.fr.swift.local.LocalFuture;
import com.fr.swift.local.LocalInvoker;
import com.fr.swift.local.LocalInvokerCreator;
import com.fr.swift.property.SwiftProperty;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * This class created on 2018/11/15
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SwiftProperty.class, ProxyServiceRegistry.class})
public class LocalInvokerTest extends BaseInvokerTest {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testSyncLocalInvoker() {
        Invoker invoker = new LocalInvokerCreator().createSyncInvoker(ITestInvoker.class, null);
        assertTrue(invoker instanceof LocalInvoker);
        assertNull(invoker.getUrl());
        Class[] parameterTypes = new Class[]{String.class, String.class, int.class, long.class};
        long time = System.currentTimeMillis();
        Object[] paramters = new Object[]{"1", "test", 20, time};
        Result result = invoker.invoke(new SwiftInvocation("print", parameterTypes, paramters));
        assertTrue(result.getValue() instanceof String);
        assertEquals(result.getValue(), "1test20" + time);
    }

    public void testAsyncLocalInvoker() {
        Invoker invoker = new LocalInvokerCreator().createAsyncInvoker(ITestInvoker.class, null);
        assertTrue(invoker instanceof LocalInvoker);
        assertNull(invoker.getUrl());
        Class[] parameterTypes = new Class[]{String.class, String.class, int.class, long.class};
        long time = System.currentTimeMillis();
        Object[] paramters = new Object[]{"1", "test", 20, time};
        Result result = invoker.invoke(new SwiftInvocation("print", parameterTypes, paramters));
        assertTrue(result.getValue() instanceof LocalFuture);
        assertEquals(((LocalFuture) result.getValue()).get(), "1test20" + time);
    }
}
