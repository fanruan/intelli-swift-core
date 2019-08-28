package com.fr.swift.exception.queue;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.exception.ExceptionInfo;
import com.fr.swift.exception.service.ExceptionInfoService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Marvin
 * @date 8/27/2019
 * @description
 * @since swift 1.1
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({BeanFactory.class, SwiftContext.class, ExceptionInfo.class})
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
public class MasterExceptionInfoQueueTest {
    @Mock
    ExceptionInfo info1;
    @Mock
    ExceptionInfo info2;
    @Mock
    ExceptionInfo info3;
    @Mock
    ExceptionInfo info4;

    Set<ExceptionInfo> infoSet;

    ExceptionInfoService infoService;

    @Before
    public void setUp() {
        infoSet = new HashSet<ExceptionInfo>() {{
            add(info1);
            add(info2);
            add(info3);
            add(info4);
        }};

        PowerMockito.mockStatic(SwiftContext.class);
        infoService = PowerMockito.mock(ExceptionInfoService.class);
        PowerMockito.mockStatic(BeanFactory.class);
        BeanFactory factory = PowerMockito.mock(BeanFactory.class);
        PowerMockito.when(SwiftContext.get()).thenReturn(factory);
        PowerMockito.when(factory.getBean(ExceptionInfoService.class)).thenReturn(infoService);

        PowerMockito.when(infoService.getUnsolvedExceptionInfo()).thenReturn(infoSet);
    }

    @Test
    public void testOffer() throws InterruptedException {
        MasterExceptionInfoQueue.getInstance().offer(info1);
        MasterExceptionInfoQueue.getInstance().offer(info2);
        MasterExceptionInfoQueue.getInstance().offer(info3);
        MasterExceptionInfoQueue.getInstance().offer(info4);

        PowerMockito.when(infoService.existsException(info4)).thenReturn(true);
        Assert.assertFalse(MasterExceptionInfoQueue.getInstance().offer(info4));

        Assert.assertTrue(infoSet.contains(MasterExceptionInfoQueue.getInstance().take()));
        Assert.assertTrue(infoSet.contains(MasterExceptionInfoQueue.getInstance().take()));
        Assert.assertTrue(infoSet.contains(MasterExceptionInfoQueue.getInstance().take()));
        Assert.assertTrue(infoSet.contains(MasterExceptionInfoQueue.getInstance().take()));
    }

    @Test
    public void testInitExceptionInfoQueue() throws InterruptedException {
        MasterExceptionInfoQueue.getInstance().initExceptionInfoQueue();

        Assert.assertTrue(infoSet.contains(MasterExceptionInfoQueue.getInstance().take()));
        Assert.assertTrue(infoSet.contains(MasterExceptionInfoQueue.getInstance().take()));
        Assert.assertTrue(infoSet.contains(MasterExceptionInfoQueue.getInstance().take()));
        Assert.assertTrue(infoSet.contains(MasterExceptionInfoQueue.getInstance().take()));
    }
}
