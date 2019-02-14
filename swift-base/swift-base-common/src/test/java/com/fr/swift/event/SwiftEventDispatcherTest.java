package com.fr.swift.event;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author anchore
 * @date 12/4/2018
 */
public class SwiftEventDispatcherTest {

    @Test
    public void testListenAndFire() throws InterruptedException {

        SwiftEventListener<Object> listener = new SwiftEventListener<Object>() {
            @Override
            public void on(Object data) {
                Assert.assertEquals("data1", data);
            }
        };
        SwiftEventDispatcher.listen(Evt.EVT1, listener);

        SwiftEventDispatcher.fire(Evt.EVT1, "data1");

        SwiftEventListener<Object> listener1 = new SwiftEventListener<Object>() {
            @Override
            public void on(Object data) {
                Assert.assertEquals("data2", data);
            }
        };
        SwiftEventDispatcher.listen(Evt.EVT2, listener1);
        SwiftEventDispatcher.fire(Evt.EVT1, "data1");
        SwiftEventDispatcher.fire(Evt.EVT2, "data2");

        Thread.sleep(1000);

        SwiftEventDispatcher.remove(listener);
        SwiftEventDispatcher.remove(listener1);
    }

    @Test
    public void testRemove() throws InterruptedException {
        final AtomicInteger i = new AtomicInteger(0);
        SwiftEventListener<Object> listener = new SwiftEventListener<Object>() {
            @Override
            public void on(Object data) {
                i.getAndIncrement();
            }
        };
        SwiftEventDispatcher.listen(Evt.EVT1, listener);
        SwiftEventDispatcher.remove(listener);
        SwiftEventDispatcher.fire(Evt.EVT1);
        Assert.assertEquals(0, i.get());

        Thread.sleep(1000);
    }

    @Test
    public void testFireBlackHole() throws InterruptedException {
        SwiftEventDispatcher.fire(Evt.EVT1);

        Thread.sleep(1000);
    }

    enum Evt implements SwiftEvent {
        EVT1, EVT2
    }
}