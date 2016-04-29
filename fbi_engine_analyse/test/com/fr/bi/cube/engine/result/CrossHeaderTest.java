package com.fr.bi.cube.engine.result;

import com.fr.bi.cube.engine.calculator.key.TargetGettingKey;
import com.fr.bi.cube.engine.index.IDGroupValueIndex;
import com.fr.bi.cube.engine.store.ColumnKey;
import junit.framework.TestCase;

/**
 * Created by Connery on 14-11-5.
 */
public class CrossHeaderTest extends TestCase {


    public void testBuildLeftRelation() {
        CrossHeader header = newCrossHeader();
        CrossHeader l = newCrossHeader();
        CrossNode node = newCrossNode();
        header.setValue(node);

        header.buildLeftRelation(l);
        assertSame(l.getValue(), node);

    }

    public void testCratePageNode() {
        CrossHeader header = newCrossHeader();
        CrossNode node = newCrossNode();
        CrossNode child = newCrossNode();
        node.addLeftChild(child);
        header.setValue(node);
        Node pageNode = header.createPageNode(0, 1);
        assertSame(pageNode, header);

        Node pageNode1 = header.createPageNode(1, 1);
        assertSame(pageNode1.getFieldName(), "good");
    }

    public void testCreateAfterCountNode() {
        CrossHeader header = newCrossHeader();
        CrossNode node = newCrossNode();
        CrossNode child = newCrossNode();
        node.addLeftChild(child);
        header.setValue(node);
        Node afterNode = header.createAfterCountNode(1);
        assertSame(((CrossHeader) afterNode).getValue(), node);
        Node afterNode1 = header.createAfterCountNode(0);
        assertSame(((CrossHeader) afterNode1).getValue(), node);

    }

    public void testCreateBeforeCountNode() {
        CrossHeader header = newCrossHeader();
        CrossNode node = newCrossNode();
        CrossNode child = newCrossNode();
        node.addLeftChild(child);
        header.setValue(node);
        Node afterNode = header.createBeforeCountNode(1);
        assertSame(((CrossHeader) afterNode).getValue(), node);
        Node afterNode1 = header.createBeforeCountNode(0);
        assertSame(((CrossHeader) afterNode1).getValue(), node);

    }

    public void testCreateCloneNodeWithValue() {
        CrossHeader header = newCrossHeader();
        CrossNode node = newCrossNode();
        CrossNode child = newCrossNode();
        node.addLeftChild(child);
        header.setValue(node);
        Node afterNode = header.createCloneNodeWithValue();
        assertSame(((CrossHeader) afterNode).getValue(), node);
        Node afterNode1 = header.createCloneNodeWithValue();
        assertSame(((CrossHeader) afterNode1).getValue(), node);
    }

    public void testCreateNewTargetValueNode() {
//        CrossHeader header= newCrossHeader();
//        CrossHeader l = newCrossHeader();
//        CrossNode node =  newCrossNode();
//        header.setValue(node);
//
//        header.buildLeftRelation(l);
//        assertSame(l.getValue(),node);
//
//    }
//    public void testCratePageNode()
//    {
//        CrossHeader header= newCrossHeader();
//        CrossNode node =  newCrossNode();
//        CrossNode child =  newCrossNode();
//        node.addLeftChild(child);
//        header.setValue(node);
//       Node pageNode =  header.createPageNode(0,1);
//        assertSame(pageNode,header);
//
//        Node pageNode1 =  header.createPageNode(1,1);
//        assertSame(pageNode1.getFieldName(), "good");
//    }
//    public void testCreateAfterCountNode()
//    {
//        CrossHeader header= newCrossHeader();
//        CrossNode node =  newCrossNode();
//        CrossNode child =  newCrossNode();
//        node.addLeftChild(child);
//        header.setValue(node);
//        Node node1 =  header.createResultFilterNodeWithTopValue();

    }

    public void testDealWithChildLeft() {
        CrossHeader header = newCrossHeader();
        CrossNode node = newCrossNode();
        CrossHeader child = newCrossHeader();
        header.setValue(node);
        CrossHeader top = newCrossHeader();
        header.addChild(child);
        header.dealWithChildLeft(top, newCrossNode());
        assertTrue(child.getValue() != null);
        assertSame(child.getValue().getHead(), top);
    }

    public void testDealWithChildLeft4Calculate() {
        CrossHeader header = newCrossHeader();
        CrossNode node = newCrossNode();
        CrossHeader child = newCrossHeader();
        header.setValue(node);
        CrossHeader top = newCrossHeader();
        header.addChild(child);
        header.dealWithChildLeft4Calculate(top, newCrossNode(), new TargetGettingKey[0]);
        assertTrue(child.getValue() != null);
        assertSame(child.getValue().getHead(), top);

    }

    public void testDealWithChildTop() {
        CrossHeader header = newCrossHeader();
        CrossNode node = newCrossNode();
        CrossHeader child = newCrossHeader();
        header.setValue(node);
        CrossHeader left = newCrossHeader();
        CrossNode baseNode = newCrossNode();
        header.addChild(child);
        header.dealWithChildTop(left, baseNode);
        assertTrue(baseNode.getTopChildLength() == 1);

    }

    public void testDealWithChildTop4Calculate() {
        CrossHeader header = newCrossHeader();
        CrossNode node = newCrossNode();
        CrossHeader child = newCrossHeader();
        header.setValue(node);
        CrossHeader left = newCrossHeader();
        CrossNode baseNode = newCrossNode();
        header.addChild(child);
        header.dealWithChildTop4Calculate(left, baseNode, new TargetGettingKey[0]);
        assertTrue(baseNode.getTopChildLength() == 1);

    }


    private CrossHeader newCrossHeader() {
        IDGroupValueIndex index = new IDGroupValueIndex(new int[5], 10);
        return new CrossHeader(new ColumnKey("good"), new Object(), index);
    }

    private CrossNode newCrossNode() {
        return new CrossNode(new CrossHeader(new ColumnKey("header"), null, null), new CrossHeader(new ColumnKey("left"), null, null));
    }


}