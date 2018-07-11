package com.fr.swift.query.group.by2.node.mapper;

import com.fr.swift.query.group.by.GroupByEntry;
import com.fr.swift.result.TopGroupNode;
import com.fr.swift.structure.stack.LimitedStack;
import com.fr.swift.util.function.BinaryFunction;

/**
 * Created by Lyon on 2018/4/28.
 */
public class TopGroupNodeRowMapper implements BinaryFunction<GroupByEntry, LimitedStack<TopGroupNode>, TopGroupNode[]> {

    @Override
    public TopGroupNode[] apply(GroupByEntry groupByEntry, LimitedStack<TopGroupNode> topGroupNodeLimitedStack) {
        // 给列向的一行设置索引，缓存在节点上
        topGroupNodeLimitedStack.peek().setTraversal(groupByEntry.getTraversal());
        return topGroupNodeLimitedStack.toList().toArray(new TopGroupNode[topGroupNodeLimitedStack.limit()]);
    }
}
