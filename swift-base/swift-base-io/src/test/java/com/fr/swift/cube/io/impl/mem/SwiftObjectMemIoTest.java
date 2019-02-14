package com.fr.swift.cube.io.impl.mem;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author anchore
 * @date 2019/1/4
 */
public class SwiftObjectMemIoTest {

    @Test
    public void get() {
        SwiftObjectMemIo<String> objectMemIo = new SwiftObjectMemIo<String>();
        objectMemIo.put(0, "abc");
        assertEquals("abc", objectMemIo.get(0));
    }

    @Test
    public void isReadable() {
        SwiftObjectMemIo<String> objectMemIo = new SwiftObjectMemIo<String>();
        assertFalse(objectMemIo.isReadable());
        objectMemIo.put(0, "abc");
        assertTrue(objectMemIo.isReadable());
    }

    @Test
    public void ensureCapacity() {
        SwiftObjectMemIo<Integer> objectMemIo = new SwiftObjectMemIo<Integer>(1);
        objectMemIo.put(0, 1);
        objectMemIo.put(1, 2);
        try {
            objectMemIo.put(BaseMemIo.MAX_ARRAY_SIZE, 3);
            fail();
        } catch (Throwable ignore) {
        }
    }

    @Test
    public void put() {
        SwiftObjectMemIo<Integer> objectMemIo = new SwiftObjectMemIo<Integer>();
        objectMemIo.put(0, 1);
    }

    @Test
    public void release() {
        SwiftObjectMemIo<Integer> objectMemIo = new SwiftObjectMemIo<Integer>(1);
        objectMemIo.release();
        try {
            objectMemIo.get(0);
            fail();
        } catch (Exception ignore) {
        }
    }
}