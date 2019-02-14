package com.fr.swift.service;

import com.fr.swift.ClusterNodeManager;
import com.fr.swift.SwiftContext;
import com.fr.swift.basics.ProxyFactory;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.bean.SwiftTablePathBean;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.config.service.SwiftTablePathService;
import com.fr.swift.cube.io.Types;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.db.Where;
import com.fr.swift.event.ClusterEvent;
import com.fr.swift.event.ClusterEventType;
import com.fr.swift.event.ClusterListenerHandler;
import com.fr.swift.event.ClusterType;
import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.segment.SegmentHelper;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.operator.delete.WhereDeleter;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.listener.RemoteSender;
import com.fr.swift.service.listener.SwiftServiceListenerManager;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.task.service.ServiceBlockingQueue;
import com.fr.swift.task.service.ServiceCallable;
import com.fr.swift.task.service.ServiceTaskExecutor;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author yee
 * @date 2019-01-09
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(value = {SwiftContext.class, SwiftServiceListenerManager.class, ProxySelector.class, SegmentHelper.class, ClusterSelector.class})
public class SwiftHistoryServiceTest {

    private HistoryService service;

    @Before
    public void setUp() throws Exception {
        service = new SwiftHistoryService();
        // Generate by Mock Plugin
        PowerMock.mockStatic(SwiftContext.class);
        SwiftContext mockSwiftContext = PowerMock.createMock(SwiftContext.class);
        EasyMock.expect(SwiftContext.get()).andReturn(mockSwiftContext).anyTimes();
        PowerMock.replay(SwiftContext.class);
        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq("localSegmentProvider"), EasyMock.eq(SwiftSegmentManager.class))).andReturn(mockSegmentManager()).anyTimes();
        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq(ServiceTaskExecutor.class))).andReturn(mockServiceTaskExecutor()).anyTimes();
        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq(SwiftTablePathService.class))).andReturn(mockSwiftTablePathService()).anyTimes();
        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq(SwiftCubePathService.class))).andReturn(mockSwiftCubePathService()).anyTimes();
        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq("segmentServiceProvider"), EasyMock.eq(SwiftSegmentService.class))).andReturn(mockSwiftSegmentService()).anyTimes();

        // Generate by Mock Plugin
        WhereDeleter mockWhereDeleter = PowerMock.createMock(WhereDeleter.class);
        ImmutableBitMap mockImmutableBitMap = PowerMock.createMock(ImmutableBitMap.class);
        EasyMock.expect(mockImmutableBitMap.isEmpty()).andReturn(true).anyTimes();
        EasyMock.expect(mockWhereDeleter.delete(EasyMock.anyObject(Where.class))).andReturn(mockImmutableBitMap).anyTimes();

        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq("decrementer"), EasyMock.notNull(SegmentKey.class))).andReturn(mockWhereDeleter).anyTimes();

        // Generate by Mock Plugin
        PowerMock.mockStatic(SegmentHelper.class);
        SegmentHelper mockSegmentHelper = PowerMock.createMock(SegmentHelper.class);
        Map<SourceKey, Set<String>> needDownload = new HashMap<SourceKey, Set<String>>();
        needDownload.put(new SourceKey("table"), Collections.singleton("seg0"));
        EasyMock.expect(SegmentHelper.checkSegmentExists(EasyMock.notNull(SwiftSegmentService.class), EasyMock.notNull(SwiftSegmentManager.class))).andReturn(needDownload).anyTimes();
        SegmentHelper.uploadRelation(EasyMock.anyObject(RelationSource.class), EasyMock.anyString());
        EasyMock.expectLastCall().anyTimes();
        SegmentHelper.download(EasyMock.notNull(String.class), EasyMock.notNull(Set.class), EasyMock.eq(true));
        EasyMock.expectLastCall().anyTimes();
        SegmentHelper.download(EasyMock.notNull(String.class), EasyMock.notNull(Set.class), EasyMock.eq(false));
        EasyMock.expectLastCall().anyTimes();
        SegmentHelper.uploadTable(EasyMock.notNull(SwiftSegmentManager.class), EasyMock.notNull(DataSource.class), EasyMock.notNull(String.class));
        EasyMock.expectLastCall().anyTimes();
        PowerMock.replay(SegmentHelper.class);

        // Generate by Mock Plugin
        PowerMock.mockStatic(SwiftServiceListenerManager.class);
        // Generate by Mock Plugin
        SwiftServiceListenerManager mockSwiftServiceListenerManager = PowerMock.createMock(SwiftServiceListenerManager.class);
        mockSwiftServiceListenerManager.registerService(EasyMock.notNull(SwiftService.class));
        EasyMock.expectLastCall().anyTimes();
        mockSwiftServiceListenerManager.unRegisterService(EasyMock.notNull(SwiftService.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(SwiftServiceListenerManager.getInstance()).andReturn(mockSwiftServiceListenerManager).anyTimes();
        PowerMock.replay(SwiftServiceListenerManager.class);

        PowerMock.replayAll();

        service.start();
    }

    private SwiftCubePathService mockSwiftCubePathService() {
        // Generate by Mock Plugin
        SwiftCubePathService mockSwiftCubePathService = PowerMock.createMock(SwiftCubePathService.class);
        EasyMock.expect(mockSwiftCubePathService.getSwiftPath()).andReturn(System.getProperty("user.dir")).anyTimes();
        PowerMock.replay(mockSwiftCubePathService);

        return mockSwiftCubePathService;
    }

    private SwiftSegmentService mockSwiftSegmentService() {
// Generate by Mock Plugin
        SwiftSegmentService mockSwiftSegmentService = PowerMock.createMock(SwiftSegmentService.class);
        EasyMock.expect(mockSwiftSegmentService.removeSegments(EasyMock.eq("table"))).andReturn(true).anyTimes();
        Map<SourceKey, List<SegmentKey>> segments = new HashMap<SourceKey, List<SegmentKey>>();
        segments.put(new SourceKey("table"), Arrays.<SegmentKey>asList(new SegmentKeyBean(new SourceKey("table"), 0, Types.StoreType.FINE_IO, SwiftDatabase.CUBE)));
        EasyMock.expect(mockSwiftSegmentService.getOwnSegments()).andReturn(segments).anyTimes();
        PowerMock.replay(mockSwiftSegmentService);
        return mockSwiftSegmentService;
    }

    private SwiftTablePathService mockSwiftTablePathService() {
        // Generate by Mock Plugin
        SwiftTablePathService mockSwiftTablePathService = PowerMock.createMock(SwiftTablePathService.class);
        EasyMock.expect(mockSwiftTablePathService.removePath(EasyMock.anyString())).andReturn(true).anyTimes();
        SwiftTablePathBean bean = new SwiftTablePathBean();
        bean.setTablePath(0);
        bean.setLastPath(-1);
        bean.setTmpDir(1);
        EasyMock.expect(mockSwiftTablePathService.get(EasyMock.anyString())).andReturn(bean).anyTimes();
        PowerMock.replay(mockSwiftTablePathService);

        return mockSwiftTablePathService;
    }

    private ServiceTaskExecutor mockServiceTaskExecutor() {
        // Generate by Mock Plugin
        ServiceTaskExecutor mockServiceTaskExecutor = new ServiceTaskExecutor() {
            @Override
            public <T> Future<T> submit(ServiceCallable<T> serviceCallable) throws InterruptedException {
                serviceCallable.run();
                // Generate by Mock Plugin
                Future mockFuture = PowerMock.createMock(Future.class);
                try {
                    EasyMock.expect(mockFuture.get()).andReturn(true).anyTimes();
                } catch (ExecutionException e) {
                    fail();
                }
                PowerMock.replayAll(mockFuture);
                return mockFuture;

            }

            @Override
            public void registerQueue(String name, ServiceBlockingQueue queue) {

            }

            @Override
            public void unRegisterQueue(String name) {

            }
        };
        return mockServiceTaskExecutor;
    }

    @Test
    public void getServiceType() {
        assertEquals(ServiceType.HISTORY, service.getServiceType());
    }

    @Test
    public void truncate() throws Exception {
        service.truncate(new SourceKey("table"));
        PowerMock.verifyAll();
    }

    @Test
    public void switchToCluster() {
        // Generate by Mock Plugin
        PowerMock.mockStatic(ClusterSelector.class);
        ClusterSelector mockClusterSelector = PowerMock.createMock(ClusterSelector.class);
        ClusterNodeManager mockClusterNodeManager = PowerMock.createMock(ClusterNodeManager.class);
        EasyMock.expect(mockClusterNodeManager.getCurrentId()).andReturn("clusterId").anyTimes();
        EasyMock.expect(mockClusterSelector.getFactory()).andReturn(mockClusterNodeManager).anyTimes();
        EasyMock.expect(ClusterSelector.getInstance()).andReturn(mockClusterSelector).anyTimes();

        // Generate by Mock Plugin
        PowerMock.mockStatic(ProxySelector.class);
        ProxySelector mockProxySelector = PowerMock.createMock(ProxySelector.class);
        ProxyFactory mockProxyFactory = PowerMock.createMock(ProxyFactory.class);

        // Generate by Mock Plugin
        RemoteSender mockRemoteSender = PowerMock.createMock(RemoteSender.class);
        EasyMock.expect(mockRemoteSender.trigger(EasyMock.anyObject(SwiftRpcEvent.class))).andReturn(null).anyTimes();


        EasyMock.expect(mockProxyFactory.getProxy(EasyMock.eq(RemoteSender.class))).andReturn(mockRemoteSender).anyTimes();
        EasyMock.expect(mockProxySelector.getFactory()).andReturn(mockProxyFactory).anyTimes();
        EasyMock.expect(ProxySelector.getInstance()).andReturn(mockProxySelector).anyTimes();

        PowerMock.replay(ClusterSelector.class, ProxySelector.class);
        PowerMock.replayAll();
        ClusterListenerHandler.handlerEvent(new ClusterEvent(ClusterEventType.JOIN_CLUSTER, ClusterType.CONFIGURE));
        PowerMock.verifyAll();

    }

    private SwiftSegmentManager mockSegmentManager() {
        // Generate by Mock Plugin
        List<SegmentKey> list = new ArrayList<SegmentKey>();
        list.add(new SegmentKeyBean(new SourceKey("table"), 0, Types.StoreType.FINE_IO, SwiftDatabase.CUBE));
        list.add(new SegmentKeyBean(new SourceKey("table"), 1, Types.StoreType.FINE_IO, SwiftDatabase.CUBE));
        list.add(new SegmentKeyBean(new SourceKey("table"), 2, Types.StoreType.FINE_IO, SwiftDatabase.CUBE));
        SwiftSegmentManager mockSwiftSegmentManager = PowerMock.createMock(SwiftSegmentManager.class);
        EasyMock.expect(mockSwiftSegmentManager.getSegmentKeys(EasyMock.anyObject(SourceKey.class))).andReturn(list).anyTimes();
        EasyMock.expect(mockSwiftSegmentManager.existsSegment(EasyMock.anyObject(SegmentKey.class))).andReturn(true).anyTimes();
        PowerMock.replay(mockSwiftSegmentManager);

        return mockSwiftSegmentManager;
    }

    @After
    public void tearDown() throws Exception {
        service.shutdown();
    }
}