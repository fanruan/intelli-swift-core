package com.fr.swift.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.basic.Destination;
import com.fr.swift.basic.URL;
import com.fr.swift.basics.AsyncRpcCallback;
import com.fr.swift.basics.Invocation;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.Result;
import com.fr.swift.basics.RpcFuture;
import com.fr.swift.basics.UrlFactory;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.AbstractRpcFuture;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.cluster.ClusterEntity;
import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.config.DataSyncRule;
import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.service.DataSyncRuleService;
import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.cube.io.Types;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.event.base.AbstractAnalyseRpcEvent;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.service.handler.analyse.SwiftAnalyseEventHandler;
import com.fr.swift.service.handler.global.SwiftGlobalEventHandler;
import com.fr.swift.service.handler.history.SwiftHistoryEventHandler;
import com.fr.swift.service.handler.indexing.SwiftIndexingEventHandler;
import com.fr.swift.service.handler.realtime.SwiftRealTimeEventHandler;
import com.fr.swift.source.SourceKey;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author yee
 * @date 2019-01-09
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(value = {SwiftContext.class, UrlSelector.class, ClusterSwiftServerService.class})
public class SwiftSyncDataProcessHandlerTest {
    @Before
    public void setUp() throws Exception {
        // Generate by Mock Plugin
        PowerMock.mockStatic(SwiftContext.class);
        BeanFactory mockBeanFactory = PowerMock.createMock(BeanFactory.class);
        EasyMock.expect(mockBeanFactory.getBean(EasyMock.eq(DataSyncRuleService.class))).andReturn(mockDataSyncRuleService()).anyTimes();
        EasyMock.expect(mockBeanFactory.getBean(EasyMock.eq(SwiftClusterSegmentService.class))).andReturn(mockSwiftClusterSegmentService()).anyTimes();
        EasyMock.expect(mockBeanFactory.getBean(EasyMock.eq(SwiftHistoryEventHandler.class))).andReturn(null).anyTimes();
        EasyMock.expect(mockBeanFactory.getBean(EasyMock.eq(SwiftAnalyseEventHandler.class))).andReturn(mockSwiftAnalyseEventHandler()).anyTimes();
        EasyMock.expect(mockBeanFactory.getBean(EasyMock.eq(SwiftRealTimeEventHandler.class))).andReturn(null).anyTimes();
        EasyMock.expect(mockBeanFactory.getBean(EasyMock.eq(SwiftIndexingEventHandler.class))).andReturn(null).anyTimes();
        EasyMock.expect(mockBeanFactory.getBean(EasyMock.eq(SwiftGlobalEventHandler.class))).andReturn(null).anyTimes();
        EasyMock.expect(SwiftContext.get()).andReturn(mockBeanFactory).anyTimes();
        PowerMock.replay(SwiftContext.class);
        mockUrlSelector();
        mockServerService();
        PowerMock.replayAll();

    }

    private SwiftAnalyseEventHandler mockSwiftAnalyseEventHandler() {
        // Generate by Mock Plugin
        SwiftAnalyseEventHandler mockSwiftAnalyseEventHandler = PowerMock.createMock(SwiftAnalyseEventHandler.class);
        EasyMock.expect(mockSwiftAnalyseEventHandler.handle(EasyMock.anyObject(AbstractAnalyseRpcEvent.class))).andReturn(null).anyTimes();
        PowerMock.replay(mockSwiftAnalyseEventHandler);
        return mockSwiftAnalyseEventHandler;
    }

    private SwiftClusterSegmentService mockSwiftClusterSegmentService() {
        // Generate by Mock Plugin
        SwiftClusterSegmentService mockSwiftClusterSegmentService = PowerMock.createMock(SwiftClusterSegmentService.class);
        EasyMock.expect(mockSwiftClusterSegmentService.updateSegmentTable(EasyMock.notNull(Map.class))).andReturn(true).anyTimes();
        PowerMock.replay(mockSwiftClusterSegmentService);
        return mockSwiftClusterSegmentService;

    }

    private DataSyncRuleService mockDataSyncRuleService() {
        // Generate by Mock Plugin
        DataSyncRuleService mockDataSyncRuleService = PowerMock.createMock(DataSyncRuleService.class);
        // Generate by Mock Plugin
        DataSyncRule mockDataSyncRule = PowerMock.createMock(DataSyncRule.class);
        Map<String, List<SegmentKey>> map = new HashMap<String, List<SegmentKey>>();
        map.put("clusterId", Arrays.<SegmentKey>asList(new SegmentKeyBean(new SourceKey("table"), 0, Types.StoreType.FINE_IO, SwiftDatabase.CUBE)));
        EasyMock.expect(mockDataSyncRule.getNeedLoadAndUpdateDestinations(EasyMock.notNull(Set.class), EasyMock.notNull(Set.class), EasyMock.notNull(Map.class))).andReturn(map).anyTimes();

        EasyMock.expect(mockDataSyncRuleService.getCurrentRule()).andReturn(mockDataSyncRule).anyTimes();
        PowerMock.replay(mockDataSyncRuleService, mockDataSyncRule);

        return mockDataSyncRuleService;
    }

    private void mockServerService() {
        // Generate by Mock Plugin
        PowerMock.mockStatic(ClusterSwiftServerService.class);
        ClusterSwiftServerService mockClusterSwiftServerService = PowerMock.createMock(ClusterSwiftServerService.class);
        EasyMock.expect(ClusterSwiftServerService.getInstance()).andReturn(mockClusterSwiftServerService).anyTimes();
        PowerMock.replay(ClusterSwiftServerService.class);
        Map<String, ClusterEntity> services = new HashMap<String, ClusterEntity>();
        // Generate by Mock Plugin
        URL mockURL = PowerMock.createMock(URL.class);
        // Generate by Mock Plugin
        Destination mockDestination = PowerMock.createMock(Destination.class);
        EasyMock.expect(mockDestination.getId()).andReturn("clusterId").anyTimes();

        EasyMock.expect(mockURL.getDestination()).andReturn(mockDestination).anyTimes();

        services.put("clusterId", new ClusterEntity(mockURL, ServiceType.HISTORY, HistoryService.class));
        EasyMock.expect(mockClusterSwiftServerService
                .getClusterEntityByService(EasyMock.eq(ServiceType.HISTORY)))
                .andReturn(services).anyTimes();
        PowerMock.replayAll();
    }


    private RpcFuture createFuture(final boolean success) {
        return new AbstractRpcFuture() {
            private AsyncRpcCallback callback;

            @Override
            public void done(Object o) {
                if (success) {
                    callback.success(o);
                } else {
                    callback.fail(new RuntimeException());
                }
            }

            @Override
            public RpcFuture addCallback(AsyncRpcCallback callback) {
                this.callback = callback;
                done(null);
                return this;
            }

            @Override
            public Object get() throws InterruptedException, ExecutionException {
                return null;
            }

            @Override
            public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return null;
            }
        };
    }

    private void mockUrlSelector() {
        // Generate by Mock Plugin
        PowerMock.mockStatic(UrlSelector.class);
        UrlSelector mockUrlSelector = PowerMock.createMock(UrlSelector.class);
        UrlFactory mockUrlFactory = PowerMock.createMock(UrlFactory.class);
        EasyMock.expect(mockUrlSelector.getFactory()).andReturn(mockUrlFactory).anyTimes();
        EasyMock.expect(UrlSelector.getInstance()).andReturn(mockUrlSelector).anyTimes();
        PowerMock.replay(UrlSelector.class);


        // Generate by Mock Plugin
        URL mockURL = PowerMock.createMock(URL.class);
        Destination mockDestination = PowerMock.createMock(Destination.class);
        EasyMock.expect(mockDestination.getId()).andReturn("clusterId").anyTimes();
        EasyMock.expect(mockURL.getDestination()).andReturn(mockDestination).anyTimes();


        EasyMock.expect(mockUrlFactory.getURL(EasyMock.anyString())).andReturn(mockURL).anyTimes();
        PowerMock.replayAll(mockUrlFactory, mockURL, mockDestination);
    }

    @Test
    public void processResult() throws Throwable {
// Generate by Mock Plugin
        InvokerCreator mockInvokerCreator = PowerMock.createMock(InvokerCreator.class);
        Invoker mockInvoker = PowerMock.createMock(Invoker.class);
        Result mockResult = PowerMock.createMock(Result.class);
        EasyMock.expect(mockResult.recreate()).andReturn(createFuture(true)).once();
        EasyMock.expect(mockResult.recreate()).andReturn(createFuture(false)).once();
        EasyMock.expect(mockInvoker.invoke(EasyMock.anyObject(Invocation.class))).andReturn(mockResult).anyTimes();
        EasyMock.expect(mockInvokerCreator.createAsyncInvoker(EasyMock.notNull(Class.class), EasyMock.anyObject(URL.class))).andReturn(mockInvoker).anyTimes();
        PowerMock.replayAll();
        SwiftSyncDataProcessHandler handler = new SwiftSyncDataProcessHandler(mockInvokerCreator);
        Set<SegmentKey> segmentKeys = new HashSet<SegmentKey>();
        segmentKeys.add(new SegmentKeyBean(new SourceKey("table"), 0, Types.StoreType.FINE_IO, SwiftDatabase.CUBE));
        handler.processResult(HistoryService.class.getMethod("load", Set.class, boolean.class), new Target[]{Target.HISTORY}, segmentKeys, true);
        handler.processResult(HistoryService.class.getMethod("load", Set.class, boolean.class), new Target[]{Target.HISTORY}, segmentKeys, true);
        PowerMock.verifyAll();
    }

}