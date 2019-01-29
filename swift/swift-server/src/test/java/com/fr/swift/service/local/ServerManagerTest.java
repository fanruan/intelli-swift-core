package com.fr.swift.service.local;

import com.fr.swift.SwiftContext;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.service.ServerService;
import com.fr.swift.service.manager.ServerServiceManager;
import com.fr.swift.util.ServiceBeanFactory;
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

import java.util.ArrayList;

import static junit.framework.TestCase.assertTrue;

/**
 * This class created on 2019/1/15
 *
 * @author Lucifer
 * @description
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({SwiftContext.class, ServiceBeanFactory.class, SwiftProperty.class})
public class ServerManagerTest {

    @Mock
    ServerServiceManager serverServiceManager;
    @Mock
    SwiftContext swiftContext;
    @Mock
    SwiftProperty swiftProperty;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(ServiceBeanFactory.class);
        PowerMockito.mockStatic(SwiftContext.class);
        PowerMockito.mockStatic(SwiftProperty.class);

        Mockito.when(SwiftContext.get()).thenReturn(swiftContext);
        Mockito.when(swiftContext.getBean(ServerServiceManager.class)).thenReturn(serverServiceManager);
        Mockito.when(SwiftProperty.getProperty()).thenReturn(swiftProperty);
        Mockito.when(ServiceBeanFactory.getServerServiceByNames(Mockito.<String>anySet())).thenReturn(new ArrayList());
    }

    @Test
    public void testServerManager() throws Exception {
        ServerManager serverManager = new ServerManager();
        serverManager.startUp();
        Mockito.verify(serverServiceManager).registerService(Mockito.<ServerService>anyList());
        assertTrue(serverManager.isRunning());
        serverManager.shutDown();
        Mockito.verify(serverServiceManager).unregisterService(Mockito.<ServerService>anyList());
        assertTrue(!serverManager.isRunning());
    }
}
