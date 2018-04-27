package com.fr.swift.result.node.xnode;

import com.fr.swift.result.GroupNode;

/**
 * Created by Lyon on 2018/4/9.
 */
public class XGroupNodeImpl implements XGroupNode {

    private XLeftNode xLeftNode;
    private GroupNode topGroupNode;

    public XGroupNodeImpl(XLeftNode xLeftNode, GroupNode topGroupNode) {
        this.xLeftNode = xLeftNode;
        this.topGroupNode = topGroupNode;
    }

    @Override
    public XLeftNode getCrossLeftNode() {
        return xLeftNode;
    }

    @Override
    public GroupNode getTopGroupNode() {
        return topGroupNode;
    }
}
