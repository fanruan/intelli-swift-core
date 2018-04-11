package com.fr.swift.result.node;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.result.node.iterator.ChildIterator;
import com.fr.swift.result.node.iterator.NLevelGroupNodeIterator;
import com.fr.swift.result.node.xnode.TopGroupNode;
import com.fr.swift.result.node.xnode.XLeftNode;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 2018/4/11.
 */
public class GroupNodeAggregateUtils {

    /**
     * 如果要显示汇总值的话，不管是否分页，根节点的汇总（如果是交叉表，包括列向和横向的汇总）都是对全部数据的汇总，
     * 暂时对于根节点汇总都在明细聚合的过程中做好（全部计算的情况下有优化空间）。下面的结果聚合不处理根节点。
     *
     * @param type
     * @param dimensionSize
     * @param root
     * @param aggregators
     * @return
     */
    public static GroupNode aggregate(NodeType type, int dimensionSize, GroupNode root, List<Aggregator> aggregators) {
        if (dimensionSize <= 1) {
            return root;
        }
        // 从倒数第二个维度的节点开始汇总
        int second2LastLevel = dimensionSize - 1;
        for (int nthLevel = second2LastLevel; nthLevel > 0; nthLevel--) {
            Iterator<GroupNode> iterator = new NLevelGroupNodeIterator(nthLevel, root);
            while (iterator.hasNext()) {
                GroupNode node = iterator.next();
                if (type == NodeType.GROUP) {
                    mergeChildNode(node, aggregators);
                } else  {
                    mergeChildNode(type, node, aggregators);
                }
            }
        }
        return root;
    }

    /**
     * 合并XLeftNode或者TopGroupNode
     *
     * @param type
     * @param groupNode
     * @param aggregators
     */
    private static void mergeChildNode(NodeType type, GroupNode groupNode, List<Aggregator> aggregators) {
        // >= 两个子节点才汇总
        if (groupNode.getChildrenSize() <= 1) {
            return;
        }
        Iterator<GroupNode> iterator = new ChildIterator(groupNode);
        List<AggregatorValue[]> valuesListOfParent;
        if (type == NodeType.X_LEFT) {
            valuesListOfParent = ((XLeftNode) groupNode).getValueArrayList();
        } else {
            valuesListOfParent = ((TopGroupNode) groupNode).getTopGroupValues();
        }
        while (iterator.hasNext()) {
            List<AggregatorValue[]> valuesListOfChild;
            if (type == NodeType.X_LEFT) {
                valuesListOfChild = ((XLeftNode) iterator.next()).getValueArrayList();
            } else {
                valuesListOfChild = ((TopGroupNode) iterator.next()).getTopGroupValues();
            }
            for (int i = 0; i < valuesListOfParent.size(); i++) {
                AggregatorValue[] valuesOfChild = valuesListOfChild.get(i);
                AggregatorValue[] valuesOfParent = valuesListOfParent.get(i);
                for (int j = 0; j < valuesOfParent.length; j++) {
                    aggregators.get(j).combine(valuesOfParent[j], valuesOfChild[j]);
                }
            }
        }
    }

    /**
     * 合并GroupNode
     *
     * @param groupNode
     * @param aggregators
     */
    private static void mergeChildNode(GroupNode groupNode, List<Aggregator> aggregators) {
        // >= 两个子节点才汇总
        if (groupNode.getChildrenSize() <= 1) {
            return;
        }
        Iterator<GroupNode> iterator = new ChildIterator(groupNode);
        AggregatorValue[] valuesOfParent = groupNode.getAggregatorValue();
        // aggregator的个数和values的长度要想等，不管是哪种类型的value，都要有个对应的aggregator
        assert aggregators.size() == valuesOfParent.length;
        while (iterator.hasNext()) {
            AggregatorValue[] valuesOfChild = iterator.next().getAggregatorValue();
            for (int i = 0; i < valuesOfParent.length; i++) {
                aggregators.get(i).combine(valuesOfParent[i], valuesOfChild[i]);
            }
        }
    }
}
