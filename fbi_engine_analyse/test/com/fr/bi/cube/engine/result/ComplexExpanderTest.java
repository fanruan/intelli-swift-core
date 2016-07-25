package com.fr.bi.cube.engine.result;

import com.fr.bi.cal.analyze.cal.result.ComplexExpander;
import com.fr.bi.cal.analyze.cal.result.CrossExpander;
import com.fr.bi.cal.analyze.cal.result.NodeExpander;
import junit.framework.TestCase;

import java.util.ArrayList;

/**
 * Created by Connery on 14-11-6.
 */
public class ComplexExpanderTest extends TestCase {


    public void testGetXExpander() {
        ComplexExpander expander = newComplexExpander();
//        error:      范围超出报错
//       NodeExpander node1 =  expander.getXExpander(-1);
        NodeExpander node2 = expander.getXExpander(0);
        NodeExpander node3 = expander.getXExpander(1);
        assertTrue(node2 != null);
        assertTrue(node3 != null);
    }

    public void testGetYExpander() {
        ComplexExpander expander = newComplexExpander();
//        error:      范围超出报错
//       NodeExpander node1 =  expander.getYExpander(-1);
        NodeExpander node2 = expander.getYExpander(0);
        NodeExpander node3 = expander.getYExpander(1);
        assertTrue(node2 != null);
        assertTrue(node3 != null);
    }

    public void testCreateCrossNode() {
        ComplexExpander expander = newComplexExpander();

        CrossExpander node2 = expander.createCrossNode();
        assertTrue(node2 != null);

    }

    public ComplexExpander newComplexExpander() {
        ComplexExpander complexExpander = new ComplexExpander();
        complexExpander.createAllexpander(new ArrayList<ArrayList<String>>(), new ArrayList<ArrayList<String>>());
        return complexExpander;
    }
}