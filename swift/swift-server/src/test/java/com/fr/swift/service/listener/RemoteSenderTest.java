package com.fr.swift.service.listener;

import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.service.SwiftService;
import junit.framework.TestCase;
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

/**
 * This class created on 2019/1/15
 *
 * @author Lucifer
 * @description
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({RemoteServiceReceiver.class})
public class RemoteSenderTest extends TestCase {

    @Mock
    RemoteServiceReceiver receiver;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(RemoteServiceReceiver.class);
        Mockito.when(RemoteServiceReceiver.getInstance()).thenReturn(receiver);
    }

    @Test
    public void testSender() {
        RemoteServiceSender remoteServiceSender = new RemoteServiceSender();
        SwiftRpcEvent event = Mockito.mock(SwiftRpcEvent.class);

        remoteServiceSender.appointTrigger(null, event);
        Mockito.verify(receiver, Mockito.times(1)).trigger(event);
        remoteServiceSender.trigger(event);
        Mockito.verify(receiver, Mockito.times(2)).trigger(event);
        SwiftService service = Mockito.mock(SwiftService.class);
        remoteServiceSender.registerService(service);
        Mockito.verify(receiver).registerService(service);
        remoteServiceSender.unRegisterService(service);
        Mockito.verify(receiver).unRegisterService(service);
    }
}
