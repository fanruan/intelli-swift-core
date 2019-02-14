package com.fr.swift.node;

import junit.framework.TestCase;

/**
 * This class created on 2019/1/3
 *
 * @author Lucifer
 * @description
 */
public class SwiftClusterNodeTest extends TestCase {

    public void testNode() {
        SwiftClusterNodeImpl node = new SwiftClusterNodeImpl("cluster1", "testName", "127.0.0.1", 8888);
        assertEquals(node.getId(), "cluster1");
        assertEquals(node.getName(), "testName");
        assertEquals(node.getIp(), "127.0.0.1");
        assertEquals(node.getPort(), 8888);
        assertEquals(node.toString(),"SwiftClusterNodeImpl{id='cluster1', name='testName', ip='127.0.0.1', port=8888}");
    }
}
