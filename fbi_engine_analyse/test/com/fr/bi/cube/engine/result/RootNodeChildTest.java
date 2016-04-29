package com.fr.bi.cube.engine.result;

import com.fr.bi.cube.engine.store.ColumnKey;
import junit.framework.TestCase;

/**
 * Created by Connery on 14-11-5.
 */
public class RootNodeChildTest extends TestCase {


    public void testAddSummaryData() {
        RootNodeChild rootNodeChild = newRootNodeChild();
        Object key = new Object();
        Node root = newNode();
        Node child = newNode();
        root.addChild(child);
        child.addSummaryValue(key, 10);
        rootNodeChild.setParent(root);
        rootNodeChild.addSummaryValue(key, 10);
        int summary = rootNodeChild.getSumaryValue(key).intValue();
        assertSame(20, summary);
    }

    public void testGetSumarySize() {
        RootNodeChild rootNodeChild = newRootNodeChild();
        Object key = new Object();
        Node root = newNode();
        Node child = newNode();
        root.addChild(child);
        child.addSummaryValue(key, 10);
        rootNodeChild.setParent(root);
        int size = rootNodeChild.getSumarySize();
        assertSame(1, size);

    }

    public void testGetSumaryValue() {
        RootNodeChild rootNodeChild = newRootNodeChild();
        Object key = new Object();
        Node root = newNode();
        Node child = newNode();
        root.addChild(child);
        child.addSummaryValue(key, 10);
        rootNodeChild.setParent(root);
        rootNodeChild.addSummaryValue(key, 10);
        int summary = rootNodeChild.getSumaryValue(key).intValue();
        assertSame(20, summary);
    }


    private RootNodeChild newRootNodeChild() {
        return new RootNodeChild(new ColumnKey("good"), new Object(), null);
    }

    private Node newNode(Object value) {
        ColumnKey columnKey = new ColumnKey();
        return new Node(columnKey, value);
    }

    private Node newNode() {
        return newNode("1");
    }
}