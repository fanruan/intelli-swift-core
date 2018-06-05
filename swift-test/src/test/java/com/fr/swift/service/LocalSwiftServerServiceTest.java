package com.fr.swift.service;

import com.fr.swift.service.listener.EventOrder;
import com.fr.swift.service.listener.EventType;
import com.fr.swift.service.listener.SwiftServiceListener;
import junit.framework.TestCase;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by pony on 2017/11/14.
 */
public class LocalSwiftServerServiceTest extends TestCase {
    private LocalSwiftServerService localSwiftServerService;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        localSwiftServerService = new LocalSwiftServerService();
        localSwiftServerService.start();
        new SwiftAnalyseService().start();
    }

    public void testRegisterService() throws Exception {
        Class c = LocalSwiftServerService.class;
        Field indexingService = c.getDeclaredField("indexingService");
        indexingService.setAccessible(true);
        Field analyseService = c.getDeclaredField("analyseService");
        analyseService.setAccessible(true);
        assertNotNull(analyseService.get(localSwiftServerService));
        assertNull(indexingService.get(localSwiftServerService));
    }

    public void testAddListener() {
        final AtomicInteger value = new AtomicInteger(0);
        localSwiftServerService.addListener(new SwiftServiceListener<Integer>() {
            @Override
            public void handle(SwiftServiceEvent<Integer> event) {
                value.set(event.getContent());
            }

            @Override
            public EventType getType() {
                return EventType.INDEXING_FINISH;
            }

            @Override
            public EventOrder getOrder() {
                return EventOrder.AFTER;
            }
        });
        localSwiftServerService.trigger(new SwiftServiceEvent<Integer>() {
            @Override
            public Integer getContent() {
                return 1;
            }

            @Override
            public EventType getEventType() {
                return EventType.REAL_TIME_INDEX_FINISH;
            }
        });
        assertEquals(value.get(), 0);
        localSwiftServerService.trigger(new SwiftServiceEvent<Integer>() {
            @Override
            public Integer getContent() {
                return 2;
            }

            @Override
            public EventType getEventType() {
                return EventType.INDEXING_FINISH;
            }
        });
        assertEquals(value.get(), 2);
    }

}