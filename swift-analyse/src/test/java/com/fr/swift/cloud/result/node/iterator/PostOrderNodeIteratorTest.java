package com.fr.swift.cloud.result.node.iterator;

import com.fr.swift.cloud.result.GroupNode;
import com.fr.swift.cloud.result.SwiftNode;
import junit.framework.TestCase;

import java.util.Iterator;

/**
 * Created by Lyon on 2018/4/11.
 */
public class PostOrderNodeIteratorTest extends TestCase {

    private GroupNode node;

    @Override
    public void setUp() {
        // 定义 T = { key, C }, C = {T1, T2, ...}, Ti E T
        // node = {'a', {{'b', {}}, {'c', {'e', {}}}, {'d', {}}}}
        node = new GroupNode(-1, "a");
        node.addChild(new GroupNode(1, "b"));
        GroupNode n = new GroupNode(1, "c");
        n.addChild(new GroupNode(2, "e"));
        node.addChild(n);
        node.addChild(new GroupNode(1, "d"));
    }

    public void test() {
        Iterator<SwiftNode> iterator = new PostOrderNodeIterator(3, node);
        assertTrue(iterator.hasNext());
        assertTrue(iterator.next().getData().equals("b"));
        assertTrue(iterator.next().getData().equals("e"));
        assertTrue(iterator.next().getData().equals("c"));
        assertTrue(iterator.next().getData().equals("d"));
        assertTrue(iterator.next().getData().equals("a"));
    }
}
