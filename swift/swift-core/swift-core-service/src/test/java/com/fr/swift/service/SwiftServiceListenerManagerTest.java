package com.fr.swift.service;

import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.service.listener.SwiftServiceEventHandler;
import com.fr.swift.service.listener.SwiftServiceListenerManager;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IAnswer;

/**
 * This class created on 2019/1/4
 *
 * @author Lucifer
 * @description
 */
public class SwiftServiceListenerManagerTest extends TestCase {

    public void testName() {
        assertNotNull(SwiftServiceListenerManager.getInstance());
        SwiftService swiftService = EasyMock.createMock(SwiftService.class);
        SwiftRpcEvent event = EasyMock.createMock(SwiftRpcEvent.class);
        SwiftServiceEventHandler swiftServiceListenerHandler = EasyMock.createMock(SwiftServiceEventHandler.class);

        swiftServiceListenerHandler.registerService(swiftService);
        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                throw new RuntimeException("register");
            }
        });
        swiftServiceListenerHandler.unRegisterService(swiftService);
        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                throw new RuntimeException("unregister");
            }
        });
        swiftServiceListenerHandler.trigger(event);
        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                throw new RuntimeException("trigger");
            }
        });
        EasyMock.replay(swiftService, event, swiftServiceListenerHandler);

        try {
            SwiftServiceListenerManager.getInstance().registerService(swiftService);
            assertTrue(false);
        } catch (SwiftServiceException e) {
            assertTrue(true);
        }

        SwiftServiceListenerManager.getInstance().registerHandler(swiftServiceListenerHandler);
        try {
            SwiftServiceListenerManager.getInstance().registerService(swiftService);
            assertTrue(false);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "register");
        }
        try {
            SwiftServiceListenerManager.getInstance().unRegisterService(swiftService);
            assertTrue(false);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "unregister");
        }
        try {
            SwiftServiceListenerManager.getInstance().triggerEvent(event);
            assertTrue(false);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "trigger");
        }

    }
}
