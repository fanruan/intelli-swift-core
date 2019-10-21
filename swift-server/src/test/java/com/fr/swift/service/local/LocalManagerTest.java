package com.fr.swift.service.local;

import com.fr.swift.SwiftContext;
import com.fr.swift.executor.ExecutorManager;
import com.fr.swift.executor.dispatcher.TaskDispatcher;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.service.SwiftService;
import com.fr.swift.service.executor.CollateExecutor;
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
@PrepareForTest({SwiftContext.class, ServiceBeanFactory.class, SwiftProperty.class, TaskDispatcher.class, ExecutorManager.class})
public class LocalManagerTest {
    @Mock
    LocalServiceManager localServiceManager;
    @Mock
    ServiceManager serviceManager;
    @Mock
    SwiftContext swiftContext;
    @Mock
    SwiftProperty swiftProperty;
    @Mock
    CollateExecutor collateExecutor;
    @Mock
    TaskDispatcher taskDispatcher;
    @Mock
    ExecutorManager executorManager;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(ServiceBeanFactory.class, SwiftContext.class, SwiftProperty.class, TaskDispatcher.class, ExecutorManager.class);

        Mockito.when(SwiftContext.get()).thenReturn(swiftContext);
        Mockito.when(swiftContext.getBean(ServiceManager.class)).thenReturn(serviceManager);
        Mockito.when(swiftContext.getBean(LocalServiceManager.class)).thenReturn(localServiceManager);
        Mockito.when(swiftContext.getBean(CollateExecutor.class)).thenReturn(collateExecutor);

        Mockito.when(TaskDispatcher.getInstance()).thenReturn(taskDispatcher);
        Mockito.when(ExecutorManager.getInstance()).thenReturn(executorManager);

        Mockito.when(SwiftProperty.getProperty()).thenReturn(swiftProperty);
        Mockito.when(ServiceBeanFactory.getSwiftServiceByNames(Mockito.<String>anySet())).thenReturn(new ArrayList());
    }

    @Test
    public void testLocalManager() throws Exception {
        LocalManager localManager = new LocalManager();
        localManager.startUp();
        Mockito.verify(serviceManager).startUp();
        Mockito.verify(localServiceManager).registerService(Mockito.<SwiftService>anyList());
        Mockito.verify(executorManager).pullDBTask();

        Mockito.verify(collateExecutor).start();
        assertTrue(localManager.isRunning());
        localManager.shutDown();
        Mockito.verify(executorManager).clearTasks();
        Mockito.verify(localServiceManager).unregisterService(Mockito.<SwiftService>anyList());
        Mockito.verify(collateExecutor).stop();
        assertTrue(!localManager.isRunning());
    }
}
