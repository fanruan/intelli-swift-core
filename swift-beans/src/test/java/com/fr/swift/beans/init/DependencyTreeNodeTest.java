package com.fr.swift.beans.init;

import com.fr.swift.beans.factory.init.DependencyTreeNode;
import org.junit.Before;
import org.junit.Test;

public class DependencyTreeNodeTest {
    WrapperDefinitionTest wrapperDefinitionTest = new WrapperDefinitionTest();

    DependencyTreeNode root;

    @Before
    public void init() {
        wrapperDefinitionTest.before(); //先初始化WrapperDefinition
        root = new DependencyTreeNode(wrapperDefinitionTest.definition1);
        root.next.add(new DependencyTreeNode(wrapperDefinitionTest.definition2));
        root.next.add(new DependencyTreeNode(wrapperDefinitionTest.definition3));
    }

    @Test
    public void testReplace() {
        assert root.next.size() == 2;
        DependencyTreeNode node=new DependencyTreeNode(wrapperDefinitionTest.definition1);
        node.next.add(node);
        node.next.add(node);
        node.next.add(node);
        node.next.add(node);
        root.replaceNode(node);
        assert root.next.size() == 4;
    }

    @Test
    public void testClear(){
        assert root.next.size() == 2;
        root.clear();
        assert root.next.size() == 0;
    }
}
