package com.fr.swift.result;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by lyon on 2019/1/11.
 */
public class NodeQRSImplTest {

    private int fetchSize = 200;
    private NodeQRS rs;

    @Before
    public void setUp() throws Exception {
        rs = new NodeQRSImpl(fetchSize, new GroupNode());
    }

    @Test
    public void getPage() {
        assertTrue(rs.hasNextPage());
        assertNotNull(rs.getPage());
        assertFalse(rs.hasNextPage());
    }

    @Test
    public void getMerger() {
        try {
            rs.getMerger();
            fail();
        } catch (Exception ignored) {
        }
    }

    @Test
    public void getFetchSize() {
        assertEquals(fetchSize, rs.getFetchSize());
    }

    @Test
    public void convert() {
        try {
            rs.convert(null);
            fail();
        } catch (Exception ignored) {
        }
    }
}