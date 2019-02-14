package com.fr.swift.service.local;

import com.fr.swift.SwiftContext;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.service.SwiftService;
import com.fr.swift.service.manager.LocalServiceManager;
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
public class LocalManagerTest {
    @Mock
    LocalServiceManager localServiceManager;
    @Mock
    ServiceManager serviceManager;
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
        Mockito.when(swiftContext.getBean(ServiceManager.class)).thenReturn(serviceManager);
        Mockito.when(swiftContext.getBean(LocalServiceManager.class)).thenReturn(localServiceManager);
        Mockito.when(SwiftProperty.getProperty()).thenReturn(swiftProperty);
        Mockito.when(ServiceBeanFactory.getSwiftServiceByNames(Mockito.<String>anySet())).thenReturn(new ArrayList());
    }

    @Test
    public void testLocalManager() throws Exception {
        LocalManager localManager = new LocalManager();
        localManager.startUp();
        Mockito.verify(serviceManager).startUp();
        Mockito.verify(localServiceManager).registerService(Mockito.<SwiftService>anyList());
        assertTrue(localManager.isRunning());
        localManager.shutDown();
        Mockito.verify(localServiceManager).unregisterService(Mockito.<SwiftService>anyList());
        assertTrue(!localManager.isRunning());
    }
}
