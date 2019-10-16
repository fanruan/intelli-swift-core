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
public class PrimitiveMemIoTest {

    @Test
    public void get() {
        IntMemIo intMemIo = new IntMemIo();
        intMemIo.put(0, 1);
        assertEquals(1, intMemIo.get(0));

        LongMemIo longMemIo = new LongMemIo();
        longMemIo.put(0, 1);
        assertEquals(1, longMemIo.get(0));

        DoubleMemIo doubleMemIo = new DoubleMemIo();
        doubleMemIo.put(0, 1);
        assertEquals(1, doubleMemIo.get(0), 0);
    }

    @Test
    public void isReadable() {
        IntMemIo intMemIo = new IntMemIo();
        assertFalse(intMemIo.isReadable());
        intMemIo.put(0, 1);
        assertTrue(intMemIo.isReadable());

        LongMemIo longMemIo = new LongMemIo();
        assertFalse(longMemIo.isReadable());
        longMemIo.put(0, 1);
        assertTrue(longMemIo.isReadable());

        DoubleMemIo doubleMemIo = new DoubleMemIo();
        assertFalse(doubleMemIo.isReadable());
        doubleMemIo.put(0, 1);
        assertTrue(doubleMemIo.isReadable());
    }

    @Test
    public void put() {
        IntMemIo intMemIo = new IntMemIo();
        intMemIo.put(0, 1);

        LongMemIo longMemIo = new LongMemIo();
        longMemIo.put(0, 1);

        DoubleMemIo doubleMemIo = new DoubleMemIo();
        doubleMemIo.put(0, 1);
    }

    @Test
    public void ensureCapacity() {
        IntMemIo intMemIo = new IntMemIo(1);
        intMemIo.put(0, 1);
        intMemIo.put(1, 2);
        try {
            intMemIo.put(BaseMemIo.MAX_ARRAY_SIZE, 3);
            fail();
        } catch (Throwable ignore) {
        }


        LongMemIo longMemIo = new LongMemIo(1);
        longMemIo.put(0, 1);
        longMemIo.put(1, 2);
        try {
            longMemIo.put(BaseMemIo.MAX_ARRAY_SIZE, 3);
            fail();
        } catch (Throwable ignore) {
        }


        DoubleMemIo doubleMemIo = new DoubleMemIo(1);
        doubleMemIo.put(0, 1);
        doubleMemIo.put(1, 2);
        try {
            doubleMemIo.put(BaseMemIo.MAX_ARRAY_SIZE, 3);
            fail();
        } catch (Throwable ignore) {
        }
    }

    @Test
    public void release() {
        IntMemIo intMemIo = new IntMemIo(1);
        intMemIo.release();
        try {
            intMemIo.get(0);
            fail();
        } catch (Exception ignore) {
        }

        LongMemIo longMemIo = new LongMemIo(1);
        longMemIo.release();
        try {
            longMemIo.get(0);
            fail();
        } catch (Exception ignore) {
        }

        DoubleMemIo doubleMemIo = new DoubleMemIo(1);
        doubleMemIo.release();
        try {
            doubleMemIo.get(0);
            fail();
        } catch (Exception ignore) {
        }
    }
}