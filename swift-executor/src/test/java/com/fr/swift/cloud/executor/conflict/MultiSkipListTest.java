package com.fr.swift.cloud.executor.conflict;

import org.junit.Test;

import java.util.Comparator;

/**
 * @author lucifer
 * @date 2020/4/13
 * @description
 * @since swift1.0
 */
public class MultiSkipListTest {

    @Test
    public void test() {
        MultiSkipList<Integer> multiSkipList = new MultiSkipList<>(Integer::compare);
        long start = System.currentTimeMillis();
//        multiSkipList.print();
        for (int i = 0; i < 10; i++) {
            multiSkipList.add(i);
//            multiSkipList.print();
        }
        System.out.println(multiSkipList.levels);
        System.out.println(System.currentTimeMillis() - start);
        start = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            multiSkipList.remove(i);
//            multiSkipList.print();
        }
        System.out.println(System.currentTimeMillis() - start);
    }

    @Test
    public void testAdd() {
        TestNode node1 = new TestNode(3, "node1");
        TestNode node2 = new TestNode(3, "node2");
        TestNode node3 = new TestNode(3, "node3");
        TestNode node4 = new TestNode(3, "node4");
        TestNode node5 = new TestNode(3, "node5");
        TestNode node6 = new TestNode(3, "node6");
        TestNode node7 = new TestNode(5, "node7");
        TestNode node8 = new TestNode(4, "node8");
        MultiSkipList<TestNode> multiSkipList = new MultiSkipList<>(Comparator.comparingInt(TestNode::getPriority));
        multiSkipList.add(node1);
        multiSkipList.add(node2);
        multiSkipList.add(node3);
        multiSkipList.add(node4);
        multiSkipList.add(node5);
        multiSkipList.add(node6);
        multiSkipList.add(node7);
        multiSkipList.add(node8);
        multiSkipList.print();
        multiSkipList.remove(node4);
        multiSkipList.print();
        multiSkipList.add(node4, 1);
        multiSkipList.print();
        multiSkipList.add(new TestNode(3, "node9"));
        multiSkipList.add(new TestNode(4, "node10"));
        multiSkipList.print();
    }
}