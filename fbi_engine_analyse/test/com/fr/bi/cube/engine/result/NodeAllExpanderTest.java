package com.fr.bi.cube.engine.result;

import junit.framework.TestCase;

/**
 * Created by Connery on 14-11-5.
 */
public class NodeAllExpanderTest extends TestCase {

    public void testGetChildExpander() {
        NodeAllExpander expander = new NodeAllExpander();
        assertSame(expander, expander.getChildExpander("1"));
        NodeAllExpander expander1 = new NodeAllExpander(1);
        assertTrue(expander != expander1.getChildExpander("1"));
        NodeAllExpander expander2 = new NodeAllExpander(-10);
        assertTrue(expander2.getChildExpander("-10") == null);
    }
}