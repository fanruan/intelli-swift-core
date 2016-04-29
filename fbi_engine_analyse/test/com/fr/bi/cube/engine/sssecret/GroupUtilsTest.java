package com.fr.bi.cube.engine.sssecret;

import junit.framework.TestCase;

/**
 * Created by Connery on 2014/12/30.
 */
public class GroupUtilsTest extends TestCase {
//    public void testMerge_empty() {
//        GroupConnectionValue[] groupConnectionValues = new GroupConnectionValue[1];
//        Node node = new Node(new ColumnKey(), new Object());
//        NodeDimensionIterator[] nodeDimensionIterators = new NodeDimensionIterator[1];
//        TargetGettingKey[] targetGettingKeys = new TargetGettingKey[1];
//        NodeDimensionIterator nodeDimensionIterator = EasyMock.createMock(NodeDimensionIterator.class);
//
//        nodeDimensionIterators[0] = nodeDimensionIterator;
//        Operator operator = EasyMock.createMock(Operator.class);
//        GroupUtils.merge(groupConnectionValues, node, nodeDimensionIterators, targetGettingKeys, operator);
//    }
//
//    public void testMerge() {
//        GroupConnectionValue[] groupConnectionValues = new GroupConnectionValue[1];
//        Node node = new Node(new ColumnKey(), new Object());
//        NodeDimensionIterator[] nodeDimensionIterators = new NodeDimensionIterator[1];
//        TargetGettingKey[] targetGettingKeys = new TargetGettingKey[1];
//        NodeDimensionIterator nodeDimensionIterator = EasyMock.createNiceMock(NodeDimensionIterator.class);
//
//        GroupConnectionValue groupConnectionValue = generatorGroupConnectionValue("A");
//        GroupConnectionValue groupConnectionValue_child = generatorGroupConnectionValue("B");
//        groupConnectionValue_child.setParent(groupConnectionValue);
//        EasyMock.expect(nodeDimensionIterator.next()).andReturn(groupConnectionValue).times(1);
//        nodeDimensionIterators[0] = nodeDimensionIterator;
//        Operator operator = EasyMock.createNiceMock(Operator.class);
//        EasyMock.replay(nodeDimensionIterator);
//        EasyMock.replay(operator);
//        GroupUtils.merge(groupConnectionValues, node, nodeDimensionIterators, targetGettingKeys, operator);
//        assertEquals(1, node.getChildLength());
//    }
//
//    public void testMerge_normal() {
//        GroupConnectionValue[] groupConnectionValues = new GroupConnectionValue[1];
//        Node node = new Node(new ColumnKey(), new Object());
//        NodeDimensionIterator[] nodeDimensionIterators = new NodeDimensionIterator[1];
//        TargetGettingKey[] targetGettingKeys = new TargetGettingKey[1];
//        NodeDimensionIterator nodeDimensionIterator = EasyMock.createNiceMock(NodeDimensionIterator.class);
//
//        GroupConnectionValue groupConnectionValue_god_1 = generatorGroupConnectionValue("God_1");
//        GroupConnectionValue groupConnectionValue_god_2 = generatorGroupConnectionValue("God_1");
//        GroupConnectionValue groupConnectionValue_god_3 = generatorGroupConnectionValue("God_2");
//        GroupConnectionValue groupConnectionValue_child_1 = generatorGroupConnectionValue("god_1_son_1");
//        GroupConnectionValue groupConnectionValue_child_2 = generatorGroupConnectionValue("god_1_son_2");
//        GroupConnectionValue groupConnectionValue_child_3 = generatorGroupConnectionValue("god_2_son_1");
//      GroupConnectionValue groupConnectionValue_grandson_1 = generatorGroupConnectionValue("god_1_son_1_grand_1");
////        GroupConnectionValue groupConnectionValue_grandson_2 = generatorGroupConnectionValue("g_b_2");
////        GroupConnectionValue groupConnectionValue_grandson_3 = generatorGroupConnectionValue("g_c_1");
//
//        groupConnectionValue_child_1.setParent(groupConnectionValue_god_1);
//        groupConnectionValue_child_2.setParent(groupConnectionValue_god_2);
//        groupConnectionValue_child_3.setParent(groupConnectionValue_god_3);
//        groupConnectionValue_grandson_1.setParent(groupConnectionValue_child_1);
////        groupConnectionValue_grandson_1.setParent(groupConnectionValue_child_1);
////        groupConnectionValue_grandson_2.setParent(groupConnectionValue_child_1);
////        groupConnectionValue_grandson_3.setParent(groupConnectionValue_child_2);
//        EasyMock.expect(nodeDimensionIterator.next()).andReturn(groupConnectionValue_god_1).times(1).andReturn(groupConnectionValue_god_2).times(2).andReturn(groupConnectionValue_god_3).times(1);
//        nodeDimensionIterators[0] = nodeDimensionIterator;
//        Operator operator = EasyMock.createNiceMock(Operator.class);
//        EasyMock.replay(nodeDimensionIterator);
//        EasyMock.replay(operator);
//        GroupUtils.merge(groupConnectionValues, node, nodeDimensionIterators, targetGettingKeys, operator);
//        assertEquals(3, node.getChildLength());
//    }
//
//    private GroupConnectionValue generatorGroupConnectionValue(String name) {
//        return new GroupConnectionValue(new ColumnKey(name), new Object(), new Comparator() {
//            @Override
//            public int compare(Object o1, Object o2) {
//                return 0;
//            }
//        }, null);
//
//    }
}