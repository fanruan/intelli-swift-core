package com.fr.swift.cal.info;

import junit.framework.TestCase;

/**
 * Created by Connery on 14-11-5.
 */
public class NodeExpanderTest extends TestCase {

    public void testAddChild() {
        Expander expander = new Expander();
        expander.addChild("1");
        assertNotNull(expander.getChildExpander("1"));
    }

    public void testIsChildExpand() {
        Expander expander = new Expander();
        Expander child = new Expander();
        expander.addChild("1");
        expander.addChild("2");
        child.createAllexpander(10);
        expander.createAllexpander(10);
        assertTrue(expander.isChildExpand("0"));
        assertFalse(expander.isChildExpand("2"));
    }
}