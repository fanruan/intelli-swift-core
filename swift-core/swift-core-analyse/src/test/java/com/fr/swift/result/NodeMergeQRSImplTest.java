package com.fr.swift.result;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by lyon on 2019/1/8.
 */
public class NodeMergeQRSImplTest {

    private int fetchSize = 200;
    private NodeMergeQRSImpl rs;

    @Before
    public void setUp() throws Exception {
        rs = new NodeMergeQRSImpl(fetchSize, new GroupNode(), new ArrayList<Map<Integer, Object>>());
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