package com.fr.swift.basics.base;

import com.fr.swift.basic.URL;
import com.fr.swift.basics.Invocation;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.Result;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import java.util.concurrent.Callable;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/1/24
 */

public class PeerInvocationHandlerTest {

    @Test
    public void invoke() throws Throwable {
        URL url = mock(URL.class);
        InvokerCreator invokeCreator = mock(InvokerCreator.class);
        Invoker invoker = mock(Invoker.class);
        when(invokeCreator.createSyncInvoker(Callable.class, url)).thenReturn(invoker);

        Result result = mock(Result.class);
        doReturn(result).when(invoker).invoke(ArgumentMatchers.<Invocation>any());

        when(result.recreate()).thenReturn(true);

        assertEquals(true, new PeerInvocationHandler(Callable.class, url, invokeCreator).invoke(null, Callable.class.getMethod("call"), new Object[]{}));
    }
}