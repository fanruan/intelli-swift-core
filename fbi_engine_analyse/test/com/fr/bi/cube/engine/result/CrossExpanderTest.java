package com.fr.bi.cube.engine.result;

import junit.framework.TestCase;

/**
 * Created by Connery on 14-11-6.
 */
public class CrossExpanderTest extends TestCase {


    public void testGetXExpander() {
        NodeExpander x = new NodeExpander();
        NodeExpander y = new NodeExpander();
        CrossExpander crossExpander = new CrossExpander(x, y);
        NodeExpander node = crossExpander.getXExpander();
        assertSame(x, node);

    }

    public void testGetYExpander() {
        NodeExpander x = new NodeExpander();
        NodeExpander y = new NodeExpander();
        CrossExpander crossExpander = new CrossExpander(x, y);
        NodeExpander node = crossExpander.getYExpander();
        assertSame(y, node);

    }

    public void testCreateAllexpander() {
        NodeExpander x = new NodeExpander();
        NodeExpander y = new NodeExpander();
        CrossExpander crossExpander = new CrossExpander(x, y);
        int code1 = crossExpander.hashCode();
        crossExpander.createAllexpander(2, 2);
        int code2 = crossExpander.hashCode();

    }

}