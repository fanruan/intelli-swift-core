package com.fr.swift.rm.view;

import junit.framework.TestCase;

import java.util.Collections;

/**
 * This class created on 2019/1/7
 *
 * @author Lucifer
 * @description
 */
public class NodeJoinedViewTest extends TestCase {

    public void testNodeJoinedView() {
        assertNotNull(NodeJoinedView.getInstance());
        NodeJoinedView.getInstance().nodeJoin("test1");
        NodeJoinedView.getInstance().nodeJoin("test2");
        assertFalse(NodeJoinedView.getInstance().isEmpty());
        assertEquals(NodeJoinedView.getInstance().getNodes().size(), 2);
        NodeJoinedView.getInstance().nodeLeft("test1");
        assertEquals(NodeJoinedView.getInstance().getNodes().size(), 1);
        NodeJoinedView.getInstance().nodesRemove(Collections.singleton("test2"));
        assertTrue(NodeJoinedView.getInstance().isEmpty());
    }
}
