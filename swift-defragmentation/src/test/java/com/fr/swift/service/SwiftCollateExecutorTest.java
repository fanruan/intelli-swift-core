package com.fr.swift.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.config.service.impl.SwiftSegmentServiceProvider;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class created on 2019/1/24
 *
 * @author Lucifer
 * @description
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({SwiftContext.class, SwiftExecutors.class, ProxySelector.class})
public class SwiftCollateExecutorTest {

    SwiftCollateExecutor executor;

    @Mock
    ScheduledExecutorService executorService;

    @Mock
    BeanFactory beanFactory;

    @Mock
    SwiftSegmentServiceProvider swiftSegmentService;

    @Mock
    SourceKey sourceKey;

    @Mock
    SegmentKey segmentKey;

    @Mock
    CollateService collateService;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(SwiftExecutors.class, SwiftContext.class, ProxySelector.class);
        Mockito.when(SwiftExecutors.newScheduledThreadPool(Mockito.anyInt(), Mockito.any(PoolThreadFactory.class))).thenReturn(executorService);
        Constructor constructor = SwiftCollateExecutor.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        executor = (SwiftCollateExecutor) constructor.newInstance();
        Mockito.when(SwiftContext.get()).thenReturn(beanFactory);
        Mockito.when(beanFactory.getBean(SwiftSegmentServiceProvider.class)).thenReturn(swiftSegmentService);

        Map<SourceKey, List<SegmentKey>> allSegments = new HashMap<SourceKey, List<SegmentKey>>();
        allSegments.put(sourceKey, Collections.singletonList(segmentKey));
        Mockito.when(swiftSegmentService.getAllSegments()).thenReturn(allSegments);

        Mockito.when(ProxySelector.getProxy(CollateService.class)).thenReturn(collateService);
    }

    @Test
    public void start() {
        executor.start();
        Mockito.verify(executorService).scheduleWithFixedDelay(Mockito.any(SwiftCollateExecutor.class), Mockito.anyLong(), Mockito.anyLong(), Mockito.any(TimeUnit.class));
    }

    @Test
    public void stop() {
        executor.start();
        executor.stop();
        Mockito.verify(executorService).shutdown();
    }

    @Test
    public void run() throws Exception {
        executor.start();
        executor.run();
        Mockito.verify(collateService).appointCollate(Mockito.any(SourceKey.class), Mockito.<SegmentKey>anyList());
    }
}
