package com.fr.swift.event.base;

import junit.framework.TestCase;

/**
 * This class created on 2019/1/4
 *
 * @author Lucifer
 * @description
 */
public class BaseEventTest extends TestCase {

    public void testEventResult() {
        EventResult eventResult = new EventResult();
        eventResult.setError("error");
        eventResult.setClusterId("127.0.0.1:8080");
        eventResult.setSuccess(false);
        assertEquals(eventResult.getError(), "error");
        assertEquals(eventResult.getClusterId(), "127.0.0.1:8080");
        assertFalse(eventResult.isSuccess());

        eventResult = new EventResult("127.0.0.1:8081", true);
        assertEquals(eventResult.getClusterId(), "127.0.0.1:8081");
        assertTrue(eventResult.isSuccess());
        assertNull(eventResult.getError());


        eventResult = EventResult.success("127.0.0.1:8082");
        assertEquals(eventResult.getClusterId(), "127.0.0.1:8082");
        assertTrue(eventResult.isSuccess());
        assertNull(eventResult.getError());

        eventResult = EventResult.failed("127.0.0.1:8083", "error");
        assertEquals(eventResult.getError(), "error");
        assertEquals(eventResult.getClusterId(), "127.0.0.1:8083");
        assertFalse(eventResult.isSuccess());
    }
}