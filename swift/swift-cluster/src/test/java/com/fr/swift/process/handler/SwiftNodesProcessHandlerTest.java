package com.fr.swift.process.handler;

import com.fr.swift.basic.Destination;
import com.fr.swift.basic.URL;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.UrlFactory;
import com.fr.swift.basics.annotation.ProxyService;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.ProxyServiceRegistry;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.event.base.EventResult;
import com.fr.swift.heart.HeartBeatInfo;
import com.fr.swift.heart.NodeState;
import com.fr.swift.heart.NodeType;
import com.fr.swift.local.LocalInvoker;
import junit.framework.TestCase;
import org.easymock.EasyMock;

import java.util.Collections;
import java.util.List;

/**
 * This class created on 2019/1/3
 *
 * @author Lucifer
 * @description
 */
@ProxyService(SwiftNodesProcessHandlerTest.class)
public class SwiftNodesProcessHandlerTest extends TestCase {
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
        ProxyServiceRegistry.get().registerService(new SwiftNodesProcessHandlerTest());

        invokerCreator = EasyMock.createMock(InvokerCreator.class);
        URL url = EasyMock.createMock(URL.class);
        Destination destination = EasyMock.createMock(Destination.class);
        EasyMock.expect(url.getDestination()).andReturn(destination).anyTimes();
        EasyMock.expect(destination.getId()).andReturn("127.0.0.1:8080").anyTimes();
        Invoker invoker = new LocalInvoker(ProxyServiceRegistry.get().getService(SwiftNodesProcessHandlerTest.class.getName()), SwiftNodesProcessHandlerTest.class, url, false);
        EasyMock.expect(invokerCreator.createAsyncInvoker(SwiftNodesProcessHandlerTest.class, url)).andReturn(invoker).anyTimes();

        UrlFactory urlFactory = EasyMock.createMock(UrlFactory.class);
        UrlSelector.getInstance().switchFactory(urlFactory);
        EasyMock.expect(urlFactory.getURL(EasyMock.anyObject())).andReturn(url).anyTimes();
        EasyMock.replay(invokerCreator, url, destination, urlFactory);
    }

    public void testProcessUrl() {
        SwiftNodesProcessHandler handler = new SwiftNodesProcessHandler(invokerCreator);
        List<URL> urls = handler.processUrl(new Target[]{Target.ANALYSE}, nodeStateList);
        assertEquals(urls.size(), 1);
        assertEquals(urls.get(0).getDestination().getId(), "127.0.0.1:8080");
    }

    public void testProcessResult() {
        SwiftNodesProcessHandler handler = new SwiftNodesProcessHandler(invokerCreator);
        try {
            List<EventResult> resultList = (List<EventResult>) handler.processResult(SwiftNodesProcessHandlerTest.class.getMethod("nodesHandlerMethodSuccess", List.class), new Target[]{Target.HISTORY}, nodeStateList);
            assertEquals(resultList.size(), 1);
            assertEquals(resultList.get(0).getClusterId(), "127.0.0.1:8080");
            assertNull(resultList.get(0).getError());
            assertTrue(resultList.get(0).isSuccess());
        } catch (Throwable throwable) {
            assertTrue(false);
        }

        try {
            List<EventResult> resultList = (List<EventResult>) handler.processResult(SwiftNodesProcessHandlerTest.class.getMethod("nodesHandlerMethodError", List.class), new Target[]{Target.HISTORY}, nodeStateList);
            assertEquals(resultList.size(), 1);
            assertEquals(resultList.get(0).getClusterId(), "127.0.0.1:8080");
            assertNotNull(resultList.get(0).getError());
            assertFalse(resultList.get(0).isSuccess());
        } catch (Throwable throwable) {
            assertTrue(false);
        }
    }

    public void nodesHandlerMethodSuccess(List<NodeState> nodeStateList) {
    }

    public void nodesHandlerMethodError(List<NodeState> nodeStateList) {
        throw new RuntimeException();
    }
}
