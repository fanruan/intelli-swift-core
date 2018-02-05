package com.fr.swift.cal.info;

import junit.framework.TestCase;

/**
 * Created by Connery on 14-11-5.
 */
public class NodeAllExpanderTest extends TestCase {

    public void testGetChildExpander() {
        AllExpander expander = new AllExpander();
        assertSame(expander, expander.getChildExpander("1"));
        AllExpander expander1 = new AllExpander(1);
        assertTrue(expander != expander1.getChildExpander("1"));
        AllExpander expander2 = new AllExpander(-10);
        assertTrue(expander2.getChildExpander("-10") == null);
    }
}