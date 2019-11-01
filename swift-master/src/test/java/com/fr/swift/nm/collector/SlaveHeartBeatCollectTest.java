package com.fr.swift.nm.collector;

import com.fr.swift.SwiftContext;
import com.fr.swift.nm.service.SwiftSlaveService;
import com.fr.swift.util.concurrent.SwiftExecutors;
import junit.framework.TestCase;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * This class created on 2019/1/8
 *
 * @author Lucifer
 * @description
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SwiftContext.class, SwiftExecutors.class})
public class SlaveHeartBeatCollectTest extends TestCase {

    SwiftSlaveService swiftSlaveService;

    @Override
    public void setUp() throws Exception {
        PowerMockito.mockStatic(SwiftContext.class);
        SwiftContext context = PowerMockito.mock(SwiftContext.class);
        swiftSlaveService = PowerMockito.mock(SwiftSlaveService.class);
        PowerMockito.when(SwiftContext.get()).thenReturn(context);
        PowerMockito.when(context.getBean("swiftSlaveService", SwiftSlaveService.class)).thenReturn(swiftSlaveService);
    }

    public void testCollect() throws InterruptedException {
        PowerMockito.mockStatic(SwiftExecutors.class);
        SlaveHeartBeatCollect collect = new SlaveHeartBeatCollect();
        Thread thread = PowerMockito.mock(Thread.class);
        PowerMockito.when(SwiftExecutors.newThread(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.anyString())).thenReturn(thread);
        collect.startCollect();
        collect.stopCollect();
        Mockito.verify(thread).start();
        Mockito.verify(thread).interrupt();
    }

//    public void testRun() throws InterruptedException {
//        SlaveHeartBeatCollect collect = new SlaveHeartBeatCollect();
//        collect.startCollect();
//        Thread.sleep(1000l);
//        Mockito.verify(swiftSlaveService).syncNodeStates(Mockito.anyCollection());
//    }
}
