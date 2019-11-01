package com.fr.swift.service.manager;

import com.fr.swift.service.SwiftService;
import com.fr.swift.service.listener.SwiftServiceListenerManager;
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

import java.lang.reflect.Constructor;

/**
 * This class created on 2019/1/15
 *
 * @author Lucifer
 * @description
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({SwiftServiceListenerManager.class})
public class LocalServiceManagerTest {

    @Mock
    SwiftService swiftService;
    @Mock
    SwiftServiceListenerManager manager;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(SwiftServiceListenerManager.class);
        Mockito.when(SwiftServiceListenerManager.getInstance()).thenReturn(manager);
    }

    @Test
    public void testLocalServiceManager() throws Exception {
        Constructor constructor = LocalServiceManager.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        LocalServiceManager localServiceManager = (LocalServiceManager) constructor.newInstance();
        localServiceManager.registerService(swiftService);
        Mockito.verify(manager).registerService(swiftService);
        localServiceManager.unregisterService(swiftService);
        Mockito.verify(manager).unRegisterService(swiftService);
    }
}
