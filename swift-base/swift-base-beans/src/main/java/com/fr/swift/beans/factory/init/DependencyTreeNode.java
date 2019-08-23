package com.fr.swift.beans.factory.init;

import java.util.ArrayList;
import java.util.List;

/*
 * This class created on 2019/8/12
 *
 * @author Krysta
 * @description
 * */
public class DependencyTreeNode {
    private WrapperDefinition wrapperDefinition;

    public List<DependencyTreeNode> next = new ArrayList<>();

    public DependencyTreeNode(WrapperDefinition wrapperDefinition) {
        this.wrapperDefinition = wrapperDefinition;
    }

    public WrapperDefinition getWrapperDefinition() {
        return wrapperDefinition;
    }

    /*
     * 应用场景决定只需要替换该node下的next即可，因为树是一层一层加的
     * */
    public void replaceNode(DependencyTreeNode node) {
        next = node.next;
    }

    public void clear() {
        clearTree(this);
    }

    public void clearTree(DependencyTreeNode tree) {
        if (tree == null) return;

        for (DependencyTreeNode node : tree.next) {
            clearTree(node);
        }

        tree.next.clear();
    }
}
