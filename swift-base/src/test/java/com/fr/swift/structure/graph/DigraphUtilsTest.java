package com.fr.swift.structure.graph;

import junit.framework.TestCase;

import java.util.List;

/**
 * Created by Lyon on 2018/5/1.
 */
public class DigraphUtilsTest extends TestCase {

    public void testPostOrder() {
        Digraph<Integer> digraph = new DigraphImpl<Integer>();
        // 1 -> 2 -> 3;
        digraph.addEdge(1, 2);
        digraph.addEdge(2, 3);
        List<Integer> post = DigraphUtils.topologicalOrder(digraph);
        assertTrue(post.get(0) == 1);
        assertTrue(post.get(1) == 2);
        assertTrue(post.get(2) == 3);
        digraph.addEdge(2, 4);
        digraph.addEdge(4, 1);
    }
}
