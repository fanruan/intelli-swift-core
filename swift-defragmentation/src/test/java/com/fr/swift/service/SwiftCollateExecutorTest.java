//package com.fr.swift.service;
//
//import com.fr.swift.SwiftContext;
//import com.fr.swift.basics.base.selector.ProxySelector;
//import com.fr.swift.beans.factory.BeanFactory;
//import com.fr.swift.cube.io.Types;
//import com.fr.swift.segment.SegmentKey;
//import com.fr.swift.source.SourceKey;
//import com.fr.swift.util.concurrent.PoolThreadFactory;
//import com.fr.swift.util.concurrent.SwiftExecutors;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.MockitoJUnitRunner;
//import org.powermock.api.mockito.PowerMockito;
//import org.powermock.core.classloader.annotations.PrepareForTest;
//import org.powermock.modules.junit4.PowerMockRunner;
//import org.powermock.modules.junit4.PowerMockRunnerDelegate;
//
//import java.lang.reflect.Constructor;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
//import static org.powermock.api.mockito.PowerMockito.mock;
//import static org.powermock.api.mockito.PowerMockito.mockStatic;
//import static org.powermock.api.mockito.PowerMockito.when;
//
///**
// * This class created on 2019/1/24
// *
// * @author Lucifer
// * @description
// */
//@RunWith(PowerMockRunner.class)
//@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
//@PrepareForTest({SwiftContext.class, SwiftExecutors.class, ProxySelector.class, ClusterSelector.class, SwiftServiceInfoService.class, SwiftCollateExecutor.class})
//public class SwiftCollateExecutorTest {
//
//    SwiftCollateExecutor executor;
//
//    @Mock
//    ScheduledExecutorService executorService;
//
//    @Mock
//    BeanFactory beanFactory;
//
//    @Mock
//    SwiftSegmentServiceProvider swiftSegmentService;
//
//    @Mock
//    SourceKey sourceKey;
//
//    @Mock
//    SegmentKey segmentKey;
//
//    @Mock
//    ServiceContext serviceContext;
//
//    @Mock
//    SwiftServiceInfoService swiftServiceInfoService;
//
//    @Before
//    public void setUp() throws Exception {
//        PowerMockito.mockStatic(SwiftExecutors.class, SwiftContext.class, ProxySelector.class);
//        Mockito.when(SwiftExecutors.newScheduledThreadPool(Mockito.anyInt(), Mockito.any(PoolThreadFactory.class))).thenReturn(executorService);
//        Mockito.when(SwiftContext.get()).thenReturn(beanFactory);
//        Mockito.when(beanFactory.getBean(SwiftSegmentServiceProvider.class)).thenReturn(swiftSegmentService);
//        Mockito.when(beanFactory.getBean(SwiftServiceInfoService.class)).thenReturn(swiftServiceInfoService);
//        Constructor constructor = SwiftCollateExecutor.class.getDeclaredConstructor();
//        constructor.setAccessible(true);
//        executor = (SwiftCollateExecutor) constructor.newInstance();
//
//        Map<SourceKey, List<SegmentKey>> allSegments = new HashMap<SourceKey, List<SegmentKey>>();
//        allSegments.put(sourceKey, Collections.singletonList(segmentKey));
//        Mockito.when(swiftSegmentService.getAllSegments()).thenReturn(allSegments);
//
//        Mockito.when(ProxySelector.getProxy(ServiceContext.class)).thenReturn(serviceContext);
//        Mockito.when(segmentKey.getStoreType()).thenReturn(Types.StoreType.FINE_IO);
//    }
//
//    @Test
//    public void start() {
//        executor.start();
//        Mockito.verify(executorService).scheduleAtFixedRate(Mockito.any(SwiftCollateExecutor.class), Mockito.anyLong(), Mockito.anyLong(), Mockito.any(TimeUnit.class));
//    }
//
//    @Test
//    public void stop() {
//        executor.start();
//        executor.stop();
//        Mockito.verify(executorService).shutdown();
//    }
//
//    @Test
//    public void run1() throws Exception {
//        executor.start();
//        ClusterSelector clusterSelector = mock(ClusterSelector.class, Mockito.RETURNS_DEEP_STUBS);
//        Mockito.when(clusterSelector.getFactory().isCluster()).thenReturn(true, false);
//
//        mockStatic(ClusterSelector.class);
//        Mockito.when(ClusterSelector.getInstance()).thenReturn(clusterSelector);
//
//        executor.run();
//        Mockito.verify(serviceContext).appointCollate(Mockito.any(SourceKey.class), Mockito.<SegmentKey>anyList());
//    }
//
//    @Test
//    public void run2() throws Exception {
//        executor.start();
//        ClusterSelector clusterSelector = mock(ClusterSelector.class, Mockito.RETURNS_DEEP_STUBS);
//        Mockito.when(clusterSelector.getFactory().isCluster()).thenReturn(true);
//
//        mockStatic(ClusterSelector.class);
//        Mockito.when(ClusterSelector.getInstance()).thenReturn(clusterSelector);
//
//        SwiftServiceInfoEntity swiftServiceInfoEntity = mock(SwiftServiceInfoEntity.class);
//        List<SwiftServiceInfoEntity> swiftServiceInfoEntityList = Arrays.asList(swiftServiceInfoEntity);
//        when(swiftServiceInfoService.getServiceInfoByService(ClusterNodeService.SERVICE)).thenReturn(swiftServiceInfoEntityList);
//
//        Mockito.when(clusterSelector.getFactory().getMasterId()).thenReturn("1");
//        Mockito.when(swiftServiceInfoEntity.getClusterId()).thenReturn("1", "2");
//
//        executor.run();
//        if (swiftServiceInfoEntity.getClusterId() == "1") {
//            Mockito.verify(serviceContext).appointCollate(Mockito.any(SourceKey.class), Mockito.<SegmentKey>anyList());
//        }
//    }
//
//}
