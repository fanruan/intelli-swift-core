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
import com.fr.swift.cluster.ClusterEntity;
import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.cube.io.Types;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author yee
 * @date 2019-01-09
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(value = {ClusterSwiftServerService.class, SwiftContext.class, UrlSelector.class})
public class SwiftCommonLoadProcessHandlerTest {

    @Test
    public void processResult() throws Throwable {
        // Generate by Mock Plugin
        InvokerCreator mockInvokerCreator = PowerMock.createMock(InvokerCreator.class);
        // Generate by Mock Plugin
        Invoker mockInvoker = PowerMock.createMock(Invoker.class);
        Result mockResult = PowerMock.createMock(Result.class);
        EasyMock.expect(mockResult.recreate()).andReturn(createFuture(true)).once();
        EasyMock.expect(mockResult.recreate()).andReturn(createFuture(false)).once();
        EasyMock.expect(mockInvoker.invoke(EasyMock.anyObject(Invocation.class))).andReturn(mockResult).anyTimes();

        EasyMock.expect(mockInvokerCreator.createAsyncInvoker(EasyMock.anyObject(Class.class), EasyMock.anyObject(URL.class))).andReturn(mockInvoker).anyTimes();
        mockUrlSelector();
        PowerMock.replayAll();
        mockContext();
        mockServerService();
        Map<SegmentKey, List<String>> map = new HashMap<SegmentKey, List<String>>();
        map.put(new SegmentKeyBean(new SourceKey("table"), 0, Types.StoreType.FINE_IO, SwiftDatabase.CUBE), Arrays.asList("row_count", "allShow"));
        map.put(new SegmentKeyBean(new SourceKey("table"), 1, Types.StoreType.FINE_IO, SwiftDatabase.CUBE), Arrays.asList("row_count", "allShow"));
        SwiftCommonLoadProcessHandler handler = new SwiftCommonLoadProcessHandler(mockInvokerCreator);
        handler.processResult(HistoryService.class.getMethod("commonLoad", SourceKey.class, Map.class), new Target[]{Target.HISTORY}, new SourceKey("table"), map);
        handler.processResult(HistoryService.class.getMethod("commonLoad", SourceKey.class, Map.class), new Target[]{Target.HISTORY}, new SourceKey("table"), map);
// do test
        PowerMock.verifyAll();

    }

    private void mockContext() {
        // Generate by Mock Plugin
        PowerMock.mockStatic(SwiftContext.class);
        SwiftContext mockSwiftContext = PowerMock.createMock(SwiftContext.class);
        EasyMock.expect(SwiftContext.get()).andReturn(mockSwiftContext).anyTimes();
        PowerMock.replay(SwiftContext.class);

        // Generate by Mock Plugin
        SwiftClusterSegmentService mockSwiftClusterSegmentService = PowerMock.createMock(SwiftClusterSegmentService.class);

        Map<SourceKey, List<SegmentKey>> map = new HashMap<SourceKey, List<SegmentKey>>();
        map.put(new SourceKey("table"), Arrays.<SegmentKey>asList(
                new SegmentKeyBean(new SourceKey("table"), 0, Types.StoreType.FINE_IO, SwiftDatabase.CUBE),
                new SegmentKeyBean(new SourceKey("table"), 1, Types.StoreType.FINE_IO, SwiftDatabase.CUBE)));
        EasyMock.expect(mockSwiftClusterSegmentService.getOwnSegments(EasyMock.anyString())).andReturn(map).anyTimes();

        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq(SwiftClusterSegmentService.class))).andReturn(mockSwiftClusterSegmentService).anyTimes();
        PowerMock.replayAll();

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


        // Generate by Mock Plugin
        URL mockURL = PowerMock.createMock(URL.class);
        Destination mockDestination = PowerMock.createMock(Destination.class);
        EasyMock.expect(mockDestination.getId()).andReturn("clusterId").anyTimes();
        EasyMock.expect(mockURL.getDestination()).andReturn(mockDestination).anyTimes();


        EasyMock.expect(mockUrlFactory.getURL(EasyMock.anyString())).andReturn(mockURL).anyTimes();
        EasyMock.expect(mockUrlSelector.getFactory()).andReturn(mockUrlFactory).anyTimes();
        EasyMock.expect(UrlSelector.getInstance()).andReturn(mockUrlSelector).anyTimes();
        PowerMock.replay(UrlSelector.class);
        PowerMock.replayAll();
    }
}