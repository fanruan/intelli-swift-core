package com.fr.swift.adaptor.struct.node;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.group.Group;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.XLeftNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jonas on 2018/5/15.
 */
public class GroupTableNode2XLeftNode{
    private GroupNode groupNode;
    private XLeftNode xLeftNode;

    public GroupTableNode2XLeftNode(GroupNode groupNode) {
        this.groupNode = groupNode;
        init();
    }

    private void init() {
        xLeftNode = new XLeftNode();
        List<GroupNode> children = groupNode.getChildren();
        int childrenSize = children.size();
        if (childrenSize == 0) {
            return;
        }

        GroupNode firstNode = children.get(0);
        AggregatorValue[] aggregatorValue = firstNode.getAggregatorValue();


        // 有多少行
        for (int i = 0; i < aggregatorValue.length; i++) {
            XLeftNode newXLeftNode = new XLeftNode();
            List<AggregatorValue[]> xLeftAggregators = new ArrayList<AggregatorValue[]>();
            for (int j = 0; j < childrenSize; j++) {
                GroupNode groupNode = children.get(j);
                AggregatorValue[] aggregatorValue1 = groupNode.getAggregatorValue();
                xLeftAggregators.add(aggregatorValue1);
            }
            newXLeftNode.setXValues(xLeftAggregators);
            xLeftNode.addChild(newXLeftNode);
        }
    }

    public XLeftNode getxLeftNode() {
        return xLeftNode;
    }
}
