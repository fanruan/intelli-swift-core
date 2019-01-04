package com.fr.swift.process.handler;

import com.fr.swift.SwiftContext;
import com.fr.swift.basic.Destination;
import com.fr.swift.basic.URL;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.UrlFactory;
import com.fr.swift.basics.annotation.ProxyService;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.ProxyServiceRegistry;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.heart.HeartBeatInfo;
import com.fr.swift.heart.NodeState;
import com.fr.swift.heart.NodeType;
import com.fr.swift.local.LocalInvoker;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.SwiftService;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class created on 2019/1/3
 *
 * @author Lucifer
 * @description
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SwiftProperty.class, SwiftContext.class})
@ProxyService(SwiftAliveNodesProcessHandlerTest.class)
public class SwiftAliveNodesProcessHandlerTest extends TestCase {
    InvokerCreator invokerCreator;
    List<NodeState> nodeStateList;

    @Override
    public void setUp() throws Exception {
        NodeState nodeState = EasyMock.createMock(NodeState.class);
        HeartBeatInfo heartBeatInfo = EasyMock.createMock(HeartBeatInfo.class);
        EasyMock.expect(nodeState.getNodeType()).andReturn(NodeType.ONLINE).anyTimes();
        EasyMock.expect(heartBeatInfo.getNodeId()).andReturn("127.0.0.1:8080").anyTimes();
        EasyMock.expect(nodeState.getHeartBeatInfo()).andReturn(heartBeatInfo).anyTimes();
        EasyMock.replay(nodeState, heartBeatInfo);
        nodeStateList = Collections.singletonList(nodeState);
        ProxyServiceRegistry.get().registerService(new SwiftAliveNodesProcessHandlerTest());

        invokerCreator = EasyMock.createMock(InvokerCreator.class);
        URL url = EasyMock.createMock(URL.class);
        Destination destination = EasyMock.createMock(Destination.class);
        EasyMock.expect(url.getDestination()).andReturn(destination).anyTimes();
        EasyMock.expect(destination.getId()).andReturn("127.0.0.1:8080").anyTimes();
        Invoker invoker = new LocalInvoker(ProxyServiceRegistry.get().getService(SwiftAliveNodesProcessHandlerTest.class.getName()), SwiftAliveNodesProcessHandlerTest.class, url, false);
        EasyMock.expect(invokerCreator.createAsyncInvoker(SwiftAliveNodesProcessHandlerTest.class, url)).andReturn(invoker).anyTimes();

        UrlFactory urlFactory = EasyMock.createMock(UrlFactory.class);
        UrlSelector.getInstance().switchFactory(urlFactory);
        EasyMock.expect(urlFactory.getURL(EasyMock.anyObject())).andReturn(url).anyTimes();
        EasyMock.replay(invokerCreator, url, destination, urlFactory);

        PowerMock.mockStatic(SwiftProperty.class);
        SwiftProperty swiftProperty = EasyMock.createMock(SwiftProperty.class);
        EasyMock.expect(SwiftProperty.getProperty()).andReturn(swiftProperty).anyTimes();
        EasyMock.expect(swiftProperty.getServerAddress()).andReturn("127.0.0.1:8081").anyTimes();
        PowerMock.replay(SwiftProperty.class);
        EasyMock.replay(swiftProperty);

        SwiftServiceInfoService swiftServiceInfoService = EasyMock.createMock(SwiftServiceInfoService.class);
        EasyMock.expect(swiftServiceInfoService.getAllServiceInfo()).andReturn(new ArrayList<SwiftServiceInfoBean>()).anyTimes();
        EasyMock.expect(swiftServiceInfoService.saveOrUpdate(EasyMock.anyObject(SwiftServiceInfoBean.class))).andReturn(true).anyTimes();
        EasyMock.expect(swiftServiceInfoService.removeServiceInfo(EasyMock.anyObject(SwiftServiceInfoBean.class))).andReturn(true).anyTimes();
        EasyMock.replay(swiftServiceInfoService);

        PowerMock.mockStatic(SwiftContext.class);
        BeanFactory beanFactory = EasyMock.createMock(BeanFactory.class);
        EasyMock.expect(SwiftContext.get()).andReturn(beanFactory).anyTimes();
        EasyMock.expect(beanFactory.getBean(SwiftServiceInfoService.class)).andReturn(swiftServiceInfoService).anyTimes();
        PowerMock.replay(SwiftContext.class);
        EasyMock.replay(beanFactory);

        SwiftService swiftService = EasyMock.createMock(SwiftService.class);
        EasyMock.expect(swiftService.getServiceType()).andReturn(ServiceType.ANALYSE).anyTimes();
        EasyMock.expect(swiftService.getId()).andReturn("127.0.0.1:8080").anyTimes();
        EasyMock.replay(swiftService);
        ClusterSwiftServerService.getInstance().start();
        ClusterSwiftServerService.getInstance().initService();
        ClusterSwiftServerService.getInstance().registerService(swiftService);
    }

    public void testProcessUrl() {
        SwiftAliveNodesProcessHandler handler = new SwiftAliveNodesProcessHandler(invokerCreator);
        List<URL> urls = handler.processUrl(Target.ANALYSE);
        assertEquals(urls.size(), 1);
        assertEquals(urls.get(0).getDestination().getId(), "127.0.0.1:8080");
    }

    public void testProcessResult() {
        SwiftAliveNodesProcessHandler handler = new SwiftAliveNodesProcessHandler(invokerCreator);
        try {
            Object result = handler.processResult(
                    SwiftAliveNodesProcessHandlerTest.class.getMethod("aliveNodesMethodSuccess", String.class, String.class, List.class), Target.ANALYSE, "127.0.0.1:8080", "tableA", Collections.EMPTY_LIST);
            assertNull(result);
        } catch (Throwable throwable) {
            assertTrue(false);
        }
    }

    public void aliveNodesMethodSuccess(String clusterId, String sourceKey, List<String> segmentKeys) {

    }
}
