package com.fr.swift.segment.event;

import com.fr.swift.ClusterNodeManager;
import com.fr.swift.SwiftContext;
import com.fr.swift.basics.ProxyFactory;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.config.bean.SwiftTablePathBean;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.config.service.SwiftTablePathService;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.event.SwiftEventListener;
import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.repository.SwiftRepository;
import com.fr.swift.repository.manager.SwiftRepositoryManager;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.listener.RemoteSender;
import com.fr.swift.task.service.ServiceCallable;
import com.fr.swift.task.service.ServiceTaskExecutor;
import org.easymock.EasyMock;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.util.List;

/**
 * @author yee
 * @date 2019-01-09
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(value = {SwiftContext.class, SwiftRepositoryManager.class, SwiftProperty.class, ClusterSelector.class, ProxySelector.class})
public class BaseHistoryListenerTest {
    void init() throws InterruptedException, IOException {
        // Generate by Mock Plugin
        PowerMock.mockStatic(SwiftContext.class);
        SwiftContext mockSwiftContext = PowerMock.createMock(SwiftContext.class);
        EasyMock.expect(SwiftContext.get()).andReturn(mockSwiftContext).anyTimes();
        PowerMock.replay(SwiftContext.class);
        // Generate by Mock Plugin
        ServiceTaskExecutor mockServiceTaskExecutor = PowerMock.createMock(ServiceTaskExecutor.class);
        EasyMock.expect(mockServiceTaskExecutor.submit(EasyMock.anyObject(ServiceCallable.class))).andReturn(null).anyTimes();
        PowerMock.replay(mockServiceTaskExecutor);

        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq(ServiceTaskExecutor.class))).andReturn(mockServiceTaskExecutor).anyTimes();

        // Generate by Mock Plugin
        SwiftTablePathService mockSwiftTablePathService = PowerMock.createMock(SwiftTablePathService.class);
        SwiftTablePathBean bean = new SwiftTablePathBean();
        bean.setLastPath(0);
        bean.setTmpDir(1);
        bean.setTablePath(0);
        EasyMock.expect(mockSwiftTablePathService.get(EasyMock.anyString())).andReturn(bean).anyTimes();
        PowerMock.replay(mockSwiftTablePathService);

        EasyMock.expect(mockSwiftContext.getBean(SwiftTablePathService.class)).andReturn(mockSwiftTablePathService).anyTimes();

        mockRepositoryManager();

        SwiftEventDispatcher.listen(SyncSegmentLocationEvent.REMOVE_SEG, new SwiftEventListener<Void>() {
            @Override
            public void on(Void data) {

            }
        });

        // Generate by Mock Plugin
        SwiftSegmentService mockSwiftSegmentService = PowerMock.createMock(SwiftSegmentService.class);
        EasyMock.expect(mockSwiftSegmentService.removeSegments(EasyMock.anyObject(List.class))).andReturn(false).anyTimes();
        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq("segmentServiceProvider"), EasyMock.eq(SwiftSegmentService.class))).andReturn(mockSwiftSegmentService).anyTimes();
        PowerMock.replay(mockSwiftSegmentService);
        mockSwiftProperty();

        mockCluster();

        mockProxy();

        PowerMock.replay(mockSwiftContext);
    }

    private void mockRepositoryManager() throws IOException {
        // Generate by Mock Plugin
        PowerMock.mockStatic(SwiftRepositoryManager.class);
        SwiftRepositoryManager mockSwiftRepositoryManager = PowerMock.createMock(SwiftRepositoryManager.class);
        EasyMock.expect(SwiftRepositoryManager.getManager()).andReturn(mockSwiftRepositoryManager).anyTimes();
        PowerMock.replay(SwiftRepositoryManager.class);
        // Generate by Mock Plugin
        SwiftRepository mockSwiftRepository = PowerMock.createMock(SwiftRepository.class);
        EasyMock.expect(mockSwiftRepository.copyToRemote(EasyMock.anyString(), EasyMock.anyString())).andReturn(true).anyTimes();
        EasyMock.expect(mockSwiftRepository.zipToRemote(EasyMock.anyString(), EasyMock.anyString())).andReturn(true).anyTimes();
        EasyMock.expect(mockSwiftRepository.copyFromRemote(EasyMock.anyString(), EasyMock.anyString())).andReturn("").anyTimes();
        PowerMock.replay(mockSwiftRepository);

        EasyMock.expect(mockSwiftRepositoryManager.currentRepo()).andReturn(mockSwiftRepository).anyTimes();
        EasyMock.replay(mockSwiftRepositoryManager);

    }

    private void mockSwiftProperty() {
        // Generate by Mock Plugin
        PowerMock.mockStatic(SwiftProperty.class);
        SwiftProperty mockSwiftProperty = PowerMock.createMock(SwiftProperty.class);
        EasyMock.expect(SwiftProperty.getProperty()).andReturn(mockSwiftProperty).anyTimes();
        PowerMock.replay(SwiftProperty.class);

        EasyMock.expect(mockSwiftProperty.isCluster()).andReturn(true).anyTimes();
        EasyMock.expect(mockSwiftProperty.getClusterId()).andReturn("LOCAL").anyTimes();
        PowerMock.replay(mockSwiftProperty);
    }

    private void mockCluster() {
        // Generate by Mock Plugin
        PowerMock.mockStatic(ClusterSelector.class);
        ClusterSelector mockClusterSelector = PowerMock.createMock(ClusterSelector.class);
        EasyMock.expect(ClusterSelector.getInstance()).andReturn(mockClusterSelector).anyTimes();
        PowerMock.replay(ClusterSelector.class);

        // Generate by Mock Plugin
        ClusterNodeManager mockClusterNodeManager = PowerMock.createMock(ClusterNodeManager.class);
        EasyMock.expect(mockClusterNodeManager.getCurrentId()).andReturn("LOCAL").anyTimes();
        EasyMock.expect(mockClusterNodeManager.isMaster()).andReturn(false).anyTimes();
        EasyMock.expect(mockClusterNodeManager.isCluster()).andReturn(true).anyTimes();
        EasyMock.expect(mockClusterSelector.getFactory()).andReturn(mockClusterNodeManager).anyTimes();
        PowerMock.replay(mockClusterNodeManager, mockClusterSelector);

    }

    private void mockProxy() {
        // Generate by Mock Plugin
        PowerMock.mockStatic(ProxySelector.class);
        ProxySelector mockProxySelector = PowerMock.createMock(ProxySelector.class);
        EasyMock.expect(ProxySelector.getInstance()).andReturn(mockProxySelector).anyTimes();
        PowerMock.replay(ProxySelector.class);
        ProxyFactory mockProxyFactory = PowerMock.createMock(ProxyFactory.class);

        // Generate by Mock Plugin
        RemoteSender mockRemoteSender = PowerMock.createMock(RemoteSender.class);
        EasyMock.expect(mockRemoteSender.trigger(EasyMock.anyObject(SwiftRpcEvent.class))).andReturn(null).anyTimes();

        EasyMock.expect(mockProxyFactory.getProxy(EasyMock.eq(RemoteSender.class))).andReturn(mockRemoteSender).anyTimes();
        EasyMock.expect(mockProxySelector.getFactory()).andReturn(mockProxyFactory).anyTimes();
        PowerMock.replay(mockProxySelector, mockProxyFactory, mockRemoteSender);

    }
}
