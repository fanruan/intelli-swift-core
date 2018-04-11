package com.fr.swift.result.node.iterator;

import com.fr.swift.result.node.GroupNode;
import junit.framework.TestCase;

import java.util.Iterator;

/**
 * Created by Lyon on 2018/4/11.
 */
public class PostOrderNodeIteratorTest extends TestCase {

    private GroupNode node;

    @Override
    public void setUp() throws Exception {
        // 定义 T = { key, C }, C = {T1, T2, ...}, Ti E T
        // node = {'a', {{'b', {}}, {'c', {'e', {}}}, {'d', {}}}}
        node = new GroupNode(0, -1, 'a');
        node.addChild(new GroupNode(0, 1, 'b'));
        GroupNode n = new GroupNode(0, 1, 'c');
        n.addChild(new GroupNode(0, 2, 'e'));
        node.addChild(n);
        node.addChild(new GroupNode(0, 1, 'd'));
    }

    public void test() {
        Iterator<GroupNode> iterator = new PostOrderNodeIterator<GroupNode>(3, node);
        assertTrue(iterator.hasNext());
        assertTrue((char) iterator.next().getData() == 'b');
        assertTrue((char) iterator.next().getData() == 'e');
        assertTrue((char) iterator.next().getData() == 'c');
        assertTrue((char) iterator.next().getData() == 'd');
        assertTrue((char) iterator.next().getData() == 'a');
    }
}
