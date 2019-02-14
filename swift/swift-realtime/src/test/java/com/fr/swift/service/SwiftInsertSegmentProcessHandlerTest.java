package com.fr.swift.service;

import com.fr.swift.basic.URL;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.handler.AbstractProcessHandler;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.SourceKey;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/1/12
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({AbstractProcessHandler.class, SwiftInsertSegmentProcessHandler.class})
public class SwiftInsertSegmentProcessHandlerTest {

    @Test
    public void processResult() throws Throwable {
        Method insert = RealtimeService.class.getDeclaredMethod("insert", SourceKey.class, SwiftResultSet.class);
        InvokerCreator invokerCreator = mock(InvokerCreator.class);
        Invoker invoker = mock(Invoker.class);
        when(invokerCreator.createAsyncInvoker(ArgumentMatchers.<Class<?>>any(), ArgumentMatchers.<URL>any())).thenReturn(invoker);

        SwiftInsertSegmentProcessHandler processHandler = spy(new SwiftInsertSegmentProcessHandler(invokerCreator));
        Object returned = new Object();
        doReturn(returned).when(processHandler, "invoke", ArgumentMatchers.<Invoker>any(), eq(insert.getDeclaringClass()), eq(insert), eq(insert.getName()), eq(insert.getParameterTypes()), ArgumentMatchers.<Object[]>any());

        assertEquals(returned, processHandler.processResult(insert, new Target[]{Target.REAL_TIME}, new SourceKey("t"), mock(SwiftResultSet.class)));

        verify(invokerCreator).createSyncInvoker(insert.getDeclaringClass(), null);
        verifyPrivate(processHandler).invoke("invoke", ArgumentMatchers.<Invoker>any(), eq(insert.getDeclaringClass()), eq(insert), eq(insert.getName()), eq(insert.getParameterTypes()), ArgumentMatchers.<Object[]>any());

    }
}