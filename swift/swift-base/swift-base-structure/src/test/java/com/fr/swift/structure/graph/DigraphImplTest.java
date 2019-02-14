package com.fr.swift.structure.graph;

import junit.framework.TestCase;

import java.util.List;

/**
 * Created by Lyon on 2018/4/30.
 */
public class DigraphImplTest extends TestCase {

    private Digraph<Integer> digraph;

    @Override
    public void setUp() throws Exception {
        digraph = new DigraphImpl<Integer>();
        digraph.addEdge(4, 2);
        digraph.addEdge(2, 3);
        digraph.addEdge(3, 2);
        digraph.addEdge(4, 3);
        digraph.addEdge(3, 5);
        digraph.addEdge(5, 6);
        digraph.addEdge(7, 6);
    }

    public void testDegree() {
        assertTrue(digraph.inDegree(2) == 2);
        assertTrue(digraph.inDegree(4) == 0);
        assertTrue(digraph.outDegree(2) == 1);
        assertTrue(digraph.outDegree(4) == 2);
        assertTrue(digraph.inDegree(7) == 0);
        assertTrue(digraph.outDegree(6) == 0);
    }

    public void test() {
        assertTrue(digraph.hasCycle());
        List<Integer> cycle = digraph.cycle();
        assertTrue(!cycle.isEmpty());
        assertTrue(cycle.get(0).equals(cycle.get(cycle.size() - 1)));
    }
}
