package com.fr.swift.result.node;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.aggregator.AggregatorValueUtils;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.node.iterator.LeafNodeIterator;
import com.fr.swift.result.node.iterator.NLevelGroupNodeIterator;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 2018/4/11.
 */
public class GroupNodeAggregateUtils {

    /**
     * 使用明细Aggregator聚合metric用于过滤
     *
     * @param dimensionSize
     * @param root
     * @param aggregators
     * @return
     */
    public static GroupNode aggregateMetric(int dimensionSize, GroupNode root, List<Aggregator> aggregators) {
        return aggregate(dimensionSize, root, aggregators);
    }

    /**
     * 如果要显示汇总值的话，不管是否分页，根节点的汇总（如果是交叉表，包括列向和横向的汇总）都是对全部数据的汇总，
     * 暂时对于根节点汇总都在明细聚合的过程中做好（全部计算的情况下有优化空间）。下面的结果聚合不处理根节点。
     *
     * @param dimensionSize
     * @param root
     * @param aggregators
     * @return
     */
    public static GroupNode aggregate(int dimensionSize, GroupNode root,
                                      List<Aggregator> aggregators) {
        // 从第n个维度到第-1个维度(根节点)进行汇总
        for (int depth = dimensionSize - 1; depth >= -1; depth--) {
            Iterator<GroupNode> iterator = new NLevelGroupNodeIterator(depth, root);
            while (iterator.hasNext()) {
                GroupNode node = iterator.next();
                mergeChildGroupNode(node, aggregators);
            }
        }
        return root;
    }

    /**
     * 汇总GroupNode
     *
     * @param groupNode
     * @param aggregators
     */
    private static void mergeChildGroupNode(GroupNode groupNode, List<Aggregator> aggregators) {
        // >= 两个子节点才汇总
        if (groupNode.getChildrenSize() == 0) {
            return;
        }
        if (groupNode.getChildrenSize() == 1) {
            // 把子节点的values复制过来
            groupNode.setAggregatorValue(createAggregateValues(groupNode.getChild(0).getAggregatorValue(), aggregators));
            return;
        }
        // 节点的合计都是在叶子节点（聚合结果的二维表）的基础上做（如果没有切换汇总方式可以在聚合子节点的基础上做，后面再优化吧）
        Iterator<GroupNode> iterator = new LeafNodeIterator(groupNode);
        // 默认清空父节点的值。
        // FIXME: 2018/5/4 多个segment同时有expander的情况下父节点有可能不为空的，这时就有bug了！
        AggregatorValue[] valuesOfParent = createAggregateValues(iterator.next().getAggregatorValue(), aggregators);
        // aggregator的个数和values的长度要想等，不管是哪种类型的value，都要有个对应的aggregator
        assert aggregators.size() == valuesOfParent.length;
        while (iterator.hasNext()) {
            AggregatorValue[] valuesOfChild = iterator.next().getAggregatorValue();
            combine(valuesOfParent, valuesOfChild, aggregators);
        }
        groupNode.setAggregatorValue(valuesOfParent);
    }

    private static void combine(AggregatorValue[] valuesOfParent,
                                AggregatorValue[] valuesOfChild, List<Aggregator> aggregators) {
        for (int i = 0; i < aggregators.size(); i++) {
            Aggregator aggregator = aggregators.get(i);
            if (valuesOfParent[i] == null) {
                valuesOfParent[i] = valuesOfChild[i] == null ? null : aggregator.createAggregatorValue(valuesOfChild[i]);
            } else {
                // TODO: 2018/5/7 如果没有切换汇总方式，用明细的方式合计还是在明细汇总的基础上合计？
                valuesOfParent[i] = AggregatorValueUtils.combine(valuesOfParent[i], valuesOfChild[i], aggregator);
            }
        }
    }

    private static AggregatorValue[] createAggregateValues(AggregatorValue[] valuesOfFirstChild,
                                                           List<Aggregator> aggregators) {
        AggregatorValue[] values = Arrays.copyOf(valuesOfFirstChild, valuesOfFirstChild.length);
        for (int i = 0; i < aggregators.size(); i++) {
            Aggregator aggregator = aggregators.get(i);
            values[i] = valuesOfFirstChild[i] == null ? null : aggregator.createAggregatorValue(valuesOfFirstChild[i]);
        }
        return values;
    }
}
