package com.fr.bi.cube.engine.result;

import junit.framework.TestCase;

/**
 * Created by Connery on 14-11-5.
 */
public class NodeExpanderTest extends TestCase {

    public void testAddChild() {
        NodeExpander expander = new NodeExpander();
        NodeExpander child = new NodeExpander();
        expander.addChild("1", child);
        assertSame(child, expander.getChildExpander("1"));
        expander.addChild("0", child);
        assertSame(child, expander.getChildExpander("0"));
    }

    public void testGetChildExpander() {
        NodeExpander expander = new NodeExpander();
        NodeExpander child = new NodeExpander();
        expander.addChild("1", child);
        assertSame(child, expander.getChildExpander("1"));
        expander.addChild("0", child);
        expander.createAllexpander(10);
        NodeExpander result = expander.getChildExpander("2");
        assertTrue(result instanceof NodeAllExpander);
        assertTrue(expander.getChildExpander("1") == null);
    }

    public void testIsChildExpand() {
        NodeExpander expander = new NodeExpander();
        NodeExpander child = new NodeExpander();
        NodeExpander child1 = new NodeExpander();
        expander.addChild("1", child);
        expander.addChild("2", child1);
        child.createAllexpander(10);
        expander.createAllexpander(10);
        assertTrue(expander.isChildExpand("0"));
        assertFalse(expander.isChildExpand("2"));


    }
}