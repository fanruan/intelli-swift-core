package com.fr.bi.cube.engine.result;

import com.fr.bi.cube.engine.store.ColumnKey;
import junit.framework.TestCase;

/**
 * Created by Connery on 14-11-6.
 */
public class DiskBaseRootNodeTest extends TestCase {

    public void testGetNodeByPage() {
        DiskBaseRootNode node = new DiskBaseRootNode(new ColumnKey("good"), new Object());
//        RootNodeChild rootNodeChild =   node.getNodeByPage(1);
//        RootNodeChild rootNodeChild1 =   node.getNodeByPage(0);
//        RootNodeChild rootNodeChild2 =   node.getNodeByPage(-1);
    }

    public void testGetNewNodeByPage() {
        DiskBaseRootNode node = new DiskBaseRootNode(new ColumnKey("good"), new Object());
//       Node rootNodeChild =   node.getNewNodeByPage(1);
//        Node rootNodeChild1 =   node.getNewNodeByPage(0);
//        Node rootNodeChild2 =   node.getNewNodeByPage(-1);
    }

    public void testCreateBeforeCountNode() {
        DiskBaseRootNode node = new DiskBaseRootNode(new ColumnKey("good"), new Object());
        Node rootNodeChild = node.createBeforeCountNode(1);
        Node rootNodeChild1 = node.createBeforeCountNode(0);
        Node rootNodeChild2 = node.createBeforeCountNode(-1);
        assertTrue(rootNodeChild != null);
        assertTrue(rootNodeChild1 != null);
        assertTrue(rootNodeChild2 != null);
    }

    public void testAddNode() {
        DiskBaseRootNode node = new DiskBaseRootNode(new ColumnKey("good"), new Object());
        assertSame(0, node.getChildLength());
        node.addChild(newNode());
        assertSame(1, node.getChildLength());
        node.addChild(newNode());
        assertSame(2, node.getChildLength());
    }

    public void testGetChildLength() {
        DiskBaseRootNode node = new DiskBaseRootNode(new ColumnKey("good"), new Object());
        assertSame(0, node.getChildLength());
        node.addChild(newNode());
        assertSame(1, node.getChildLength());
        node.addChild(newNode());
        assertSame(2, node.getChildLength());
    }

    private Node newNode(Object value) {
        ColumnKey columnKey = new ColumnKey();
        return new Node(columnKey, value);
    }

    private Node newNode() {
        return newNode("1");
    }


}