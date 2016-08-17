package com.fr.bi.cube.engine.result;

import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cube.engine.calculator.key.TargetGettingKey;
import com.fr.bi.cube.engine.calculator.key.single.SumKey;
import com.fr.bi.cube.engine.store.ColumnKey;
import junit.framework.TestCase;

import java.util.Comparator;

/**
 * Created by Connery on 14-11-4.
 */
public class NodeTest extends TestCase {


    Node root;
    Node root2;
    Node child21;
    Node child22;
    Node child23;
    Node child1;
    Node child2;
    Node child3;

    Node grandson11;
    Node grandson12;
    Node grandson13;
    Node grandson21;
    Node grandson22;
    Node grandson23;
    Node grandson31;
    Node grandson32;
    Node grandson33;

    Object key1;
    Object key2;
    Object key3;


    public void testGetChild() {
        Node node = newNode("1");
        Node child1 = newNode("11");
        Node child2 = newNode("12");
        node.addChild(child1);
        node.addChild(child2);
        assertSame(child1, node.getChild(0));
        assertSame(child2, node.getChild(1));
        assertSame(child1, node.getChild("11"));
        assertSame(2, node.getChildLength());

    }

    public void testGetSummary() {
        Node node = newNode();
        Object key = createKey();
        Object key2 = createKey();
        node.addSummaryValue(key, 2);
        node.addSummaryValue(key, 3);
        node.setSumaryValue(key2, 6.0);
        assertEquals(5.0, node.getSumaryValue(key));
        assertEquals(6.0, node.getSumaryValue(key2));
        assertEquals(2, node.getSumarySize());

    }

    public void testComparator() {
        Node node_1 = newNode();
        Node node_1_same = newNode();
        Node node_1_diff = newNode();
        Node child1 = newNode("11");
        Node child2 = newNode("12");
        Node child3 = newNode("33");

        Comparator c = node_1.getComparator();

        Object key = createKey();
        Object key2 = createKey();
        Object key3 = createKey();
        node_1.addSummaryValue(key, 2);
        node_1.addSummaryValue(key2, 3);
        node_1.setSumaryValue(key3, 6.0);

        node_1_same.addSummaryValue(key, 2);
        node_1_same.addSummaryValue(key2, 3);
        node_1_same.setSumaryValue(key3, 6.0);

        node_1_diff.addSummaryValue(key, 2);
        node_1_diff.addSummaryValue(key2, 4);
        node_1_diff.setSumaryValue(key3, 6.0);
    }

    public void testAddChild() {
        Node node = newNode();
        Node child1 = newNode("11");
        Object key = createKey();

        node.addSummaryValue(key, 10);
        assertTrue((10 == node.getSumaryValue(key).floatValue()));
        node.addChild(child1);
        child1.addSummaryValue(key, 100);
        assertTrue((110 == node.getSumaryValue(key).floatValue()));
        assertTrue((100 == child1.getSumaryValue(key).floatValue()));

    }

    public void testCreateAfterCountNode() {

        Node afterNode = root.createAfterCountNode(2);
        assertTrue((2 == afterNode.getChildLength()));
        assertTrue((afterNode.childs.get(0).childs.size() == 3));
        assertTrue((afterNode.childs.get(1).childs.size() == 2));

    }

    public void testCreateBeforeCountNode() {


        Node beforeNode = root.createBeforeCountNode(4);
        assertFalse((4 == beforeNode.getChildLength()));
        assertTrue((3 == beforeNode.getChildLength()));
        assertTrue((beforeNode.childs.get(0).childs.size() == 3));
        assertTrue((beforeNode.childs.get(1).childs.size() == 3));

        Node beforeNode_1 = root.createBeforeCountNode(1);
        assertFalse((4 == beforeNode_1.getChildLength()));
        assertFalse((3 == beforeNode_1.getChildLength()));
        assertTrue((beforeNode_1.childs.get(0).childs.size() == 3));
        assertTrue((beforeNode_1.childs.get(1) == null));

    }

    public void testCreateCloneNode() {
        root.addSummaryValue(key1, 1);
        grandson11.addSummaryValue(key1, 100);
        child1.addSummaryValue(key1, 10);
        Node cloneNode = root.createCloneNode();
        assertFalse((4 == cloneNode.getChildLength()));
        assertTrue((3 == cloneNode.getChildLength()));
        assertTrue((cloneNode.childs.get(0).childs.size() == 3));
        assertTrue((cloneNode.childs.get(1).childs.size() == 3));
        assertTrue((cloneNode.childs.get(2).childs.size() == 2));
        assertTrue(cloneNode.getSumaryValue(key1) == null);

    }

    public void testCreateCloneNodeWithoutChilds() {
        root.addSummaryValue(key1, 1);
        grandson11.addSummaryValue(key1, 100);
        child1.addSummaryValue(key1, 10);
        Node cloneNode = root.createCloneNodeWithoutChild();
        assertFalse(cloneNode.getChilds() == null);
        assertFalse((4 == cloneNode.getChildLength()));
        assertFalse((3 == cloneNode.getChildLength()));
        assertTrue((0 == cloneNode.getChildLength()));

    }

    public void testCreateCloneNodeWithValue() {
        root.addSummaryValue(key1, 1);
        grandson11.addSummaryValue(key1, 100);
        child1.addSummaryValue(key1, 10);
        Node cloneNode = root.createCloneNodeWithValue();
        assertFalse((4 == cloneNode.getChildLength()));
        assertTrue((3 == cloneNode.getChildLength()));
        assertTrue((cloneNode.childs.get(0).childs.size() == 3));
        assertTrue((cloneNode.childs.get(1).childs.size() == 3));
        assertTrue((cloneNode.childs.get(2).childs.size() == 2));

        assertSame(cloneNode.getSumaryValue(key1), root.getSumaryValue(key1));

    }

    public void testGetChildAVGValue() {
//        TargetGettingKey key1 = new TargetGettingKey(new SumKey(new ColumnKey(), null),"abc");
//        child2.addSummaryValue(key1,10);
//        child3.addSummaryValue(key1,10);
//        grandson11.addSummaryValue(key1,10);
//        assertSame(10, root.getChildAVGValue(key1));
    }

    public void testAddSummaryValue() {

        Node node = newNode();
        Node child1 = newNode("11");
        Node child2 = newNode("12");
        Node grandson = newNode("33");
        node.addChild(child1);
        assertSame(child1, node.getLastChild());

        node.addChild(child2);
        assertSame(child2, node.getLastChild());
        assertSame(node, child2.getParent());
        assertSame(child2, child1.getSibling());

        child1.addChild(grandson);
        assertSame(child1, grandson.getParent());
        assertSame(node, grandson.getParent().getParent());
    }

    public void testCreateShiftCountNode() {
        Node shiftNode = root.createShiftCountNode(root2, 1);
        assertSame(3, shiftNode.getChild(0).childs.size());
        assertSame(2, shiftNode.getChild(1).childs.size());
        assertSame(3, shiftNode.getChild(2).childs.size());
    }

    public void testOrMerge() {
        TargetGettingKey key1 = new TargetGettingKey(new SumKey(new ColumnKey("good"), null), "abc");
//        root.addSummaryValue(key1,10);
//        root.OrMerge(root2,key1);
    }

    public void testCreatePageNode() {
        Node page_0_node = root.createPageNode(0, 2);
        assertTrue(page_0_node == root);
        Node page_1_node = root.createPageNode(1);
        Node page_1_shift_2_node = root.createPageNode(1, 2);

        assertSame(page_1_node.getChilds().size(), 3);
        assertSame(page_1_node.getChilds().get(0).getChilds().size(), 3);
        assertSame(page_1_node.getChilds().get(1).getChilds().size(), 3);
        assertSame(page_1_node.getChilds().get(2).getChilds().size(), 2);

        assertSame(page_1_shift_2_node.getChilds().size(), 1);
        assertSame(page_1_shift_2_node.getChilds().get(0).getChilds().size(), 2);

    }

    public void testHasNextPage() {
        assertFalse(root.hasNextPage(1));
        assertTrue(root.hasNextPage(0));
        assertFalse(root.hasNextPage(2));
    }

    public void testRelease() {
        assertTrue(root != null);
        //release     函数错误
        //       Node.release(root);
//        assertTrue(root==null);
    }


    public void setUp() {

        root = newNode();
        root2 = newNode();
        child1 = newNode();
        child2 = newNode();
        child3 = newNode();
        child21 = newNode();
        child22 = newNode();
        child23 = newNode();
        grandson11 = newNode();
        grandson12 = newNode();
        grandson13 = newNode();
        grandson21 = newNode();
        grandson22 = newNode();
        grandson23 = newNode();
        grandson31 = newNode();
        grandson32 = newNode();
        grandson33 = newNode();

        key1 = createKey();
        key2 = createKey();
        key3 = createKey();
        root.addChild(child1);
        root.addChild(child2);
        root.addChild(child3);
        child1.addChild(grandson11);
        child1.addChild(grandson12);
        child1.addChild(grandson13);
        child2.addChild(grandson21);
        child2.addChild(grandson22);
        child2.addChild(grandson23);
        child3.addChild(grandson33);
        child3.addChild(grandson31);

        root2.addChild(child21);
        root2.addChild(child22);
        root2.addChild(child23);
        child21.addChild(grandson11);
        child21.addChild(grandson12);
        child21.addChild(grandson13);
        child21.addChild(grandson21);
        child21.addChild(grandson22);
        child21.addChild(grandson23);
        child22.addChild(grandson33);

    }

    public void tearDown() {

    }

    private Object createKey() {
        return new Object();
    }

    private Node newNode(Object value) {
        ColumnKey columnKey = new ColumnKey();
        return new Node(columnKey, value);
    }

    private Node newNode() {
        return newNode("1");
    }


    private boolean isEquals(Node node1, Node node2) {
        return node1.childs.size() == node2.childs.size();
    }

}