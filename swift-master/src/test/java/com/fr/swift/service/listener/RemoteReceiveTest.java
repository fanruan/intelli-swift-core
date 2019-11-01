package com.fr.swift.service.listener;

import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.service.SwiftService;
import com.fr.swift.service.handler.SwiftServiceHandlerManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import static org.junit.Assert.assertNotNull;

/**
 * This class created on 2019/1/15
 *
 * @author Lucifer
 * @description
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({SwiftServiceHandlerManager.class, SwiftServiceListenerManager.class})
public class RemoteReceiveTest {

    @Mock
    SwiftServiceHandlerManager handlerManager;
    @Mock
    SwiftServiceListenerManager listenerManager;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(SwiftServiceHandlerManager.class);
        PowerMockito.mockStatic(SwiftServiceListenerManager.class);
        Mockito.when(SwiftServiceHandlerManager.getManager()).thenReturn(handlerManager);
        Mockito.when(SwiftServiceListenerManager.getInstance()).thenReturn(listenerManager);
    }

    @Test
    public void testReceive() throws Exception {
        assertNotNull(RemoteServiceReceiver.getInstance());
        SwiftRpcEvent event = Mockito.mock(SwiftRpcEvent.class);
        RemoteServiceReceiver.getInstance().trigger(event);
        Mockito.verify(handlerManager).handle(event);
        SwiftService service = Mockito.mock(SwiftService.class);
        RemoteServiceReceiver.getInstance().registerService(service);
        Mockito.verify(listenerManager).registerService(service);
        RemoteServiceReceiver.getInstance().unRegisterService(service);
        Mockito.verify(listenerManager).unRegisterService(service);

    }
}
