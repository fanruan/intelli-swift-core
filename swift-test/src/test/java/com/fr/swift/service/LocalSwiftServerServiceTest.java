package com.fr.swift.service;

import com.fr.swift.service.listener.EventOrder;
import com.fr.swift.service.listener.EventType;
import com.fr.swift.service.listener.SwiftServiceListener;
import com.fr.swift.test.Preparer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by pony on 2017/11/14.
 */
public class LocalSwiftServerServiceTest {
    private LocalSwiftServerService localSwiftServerService;

    @BeforeClass
    public static void boot() {
        Preparer.prepareContext();
    }

    @Before
    public void setUp() throws Exception {
        localSwiftServerService = new LocalSwiftServerService();
        localSwiftServerService.start();
        new SwiftAnalyseService().start();
    }

    @Test
    public void testRegisterService() throws Exception {
        Class c = LocalSwiftServerService.class;
        Field indexingService = c.getDeclaredField("indexingService");
        indexingService.setAccessible(true);
        Field analyseService = c.getDeclaredField("analyseService");
        analyseService.setAccessible(true);
        Assert.assertNotNull(analyseService.get(localSwiftServerService));
        Assert.assertNull(indexingService.get(localSwiftServerService));
    }

    @Test
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
        Assert.assertEquals(value.get(), 0);
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
        Assert.assertEquals(value.get(), 2);
    }

}