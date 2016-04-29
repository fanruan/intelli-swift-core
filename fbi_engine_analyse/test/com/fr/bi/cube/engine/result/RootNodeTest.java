package com.fr.bi.cube.engine.result;

import com.fr.bi.cube.engine.store.ColumnKey;
import junit.framework.TestCase;

/**
 * Created by Connery on 14-11-5.
 */
public class RootNodeTest extends TestCase {


    public void testAddNode() {
        RootNode node = newRootNode();
        RootNodeChild child = new RootNodeChild(new ColumnKey("good"), new Object());
        child.addChild(newNode());
        node.addNode(child);
        assertSame(1, node.getChildLength());
    }

    public void testGetNodeByPage() {
        RootNode node = newRootNode();
        RootNodeChild child = new RootNodeChild(new ColumnKey("good"), new Object());
        child.addChild(newNode());
        node.addNode(child);
        RootNodeChild node1 = node.getNodeByPage(1);
        assertSame(child, node1);
        assertSame(node, node1.getParent());
        assertSame("good", node1.getFieldName());

    }

    public void testGetChildLength() {
        RootNode node = newRootNode();
        RootNodeChild child = new RootNodeChild(new ColumnKey("good"), new Object());
        child.addChild(newNode());
        node.addNode(child);
        assertSame(1, node.getChildLength());
        child.addChild(newNode());
        assertSame(2, node.getChildLength());
    }

    public void testGetNewNodeByPage() {
        RootNode node = newRootNode();
        RootNodeChild child = new RootNodeChild(new ColumnKey("good"), new Object());
        Node grandson1 = newNode();
        Node grandson2 = newNode();
        child.addChild(grandson1);
        node.addNode(child);
        child.addChild(grandson2);
        Node node1 = node.getNewNodeByPage(1);
        assertSame(node1.getChilds().size(), 2);
    }


    private RootNode newRootNode() {
        return new RootNode(new ColumnKey("header"), new Object());
    }

    private Node newNode() {
        return newNode("1");
    }

    private Node newNode(Object value) {
        ColumnKey columnKey = new ColumnKey();
        return new Node(columnKey, value);
    }
}