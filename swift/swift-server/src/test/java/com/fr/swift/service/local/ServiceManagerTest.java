package com.fr.swift.service.local;

import com.fr.swift.property.SwiftProperty;
import com.fr.swift.service.SwiftService;
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

import java.util.Collections;

import static junit.framework.TestCase.assertTrue;

/**
 * This class created on 2019/1/15
 *
 * @author Lucifer
 * @description
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({ServiceBeanFactory.class, SwiftProperty.class})
public class ServiceManagerTest {

    @Mock
    SwiftService swiftService;
    @Mock
    SwiftProperty swiftProperty;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(ServiceBeanFactory.class);
        PowerMockito.mockStatic(SwiftProperty.class);
        Mockito.when(SwiftProperty.getProperty()).thenReturn(swiftProperty);
        Mockito.when(ServiceBeanFactory.getSwiftServiceByNames(Mockito.<String>anySet())).thenReturn(Collections.singletonList(swiftService));
    }

    @Test
    public void testServiceManager() throws Exception {
        ServiceManager serviceManager = new ServiceManager();
        serviceManager.startUp();
        Mockito.verify(swiftService).start();
        assertTrue(serviceManager.isRunning());
        serviceManager.shutDown();
        Mockito.verify(swiftService).shutdown();
        assertTrue(!serviceManager.isRunning());
    }
}
