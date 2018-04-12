package com.fr.swift.result.node.cal;

import com.fr.swift.query.adapter.target.cal.ResultTarget;
import com.fr.swift.query.adapter.target.cal.TargetCalculatorInfo;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.group.by.paging.MapperIterator;
import com.fr.swift.result.node.GroupNode;
import com.fr.swift.result.node.iterator.BFTGroupNodeIterator;
import com.fr.swift.result.node.xnode.XLeftNode;
import com.fr.swift.util.function.Function;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 2018/4/8.
 */
public class TargetCalculatorUtils {

    /**
     * 对node进行指标的配置类计算，并从中间计算结果指中取出最终要显示的指标
     *
     * @param root 根节点
     * @param infoList 用于配置类计算的属性
     * @param targetsForShowList 最终要返回的一组指标的位置&顺序属性
     * @return 返回处理完计算指标，并去除配置计算产生的中间结果指标的node根节点
     * @throws Exception
     */
    public static GroupNode calculate(GroupNode root, List<TargetCalculatorInfo> infoList,
                                      final List<ResultTarget> targetsForShowList) throws Exception {
        if (infoList.size() == 0) {
            return root;
        }
        List<TargetCalculator> calculators = new ArrayList<TargetCalculator>();
        for (TargetCalculatorInfo info : infoList) {
            calculators.add(TargetCalculatorFactory.create(info, root));
        }
        for (TargetCalculator calculator : calculators) {
            calculator.call();
        }
        if (root instanceof XLeftNode) {
            return getShowTargetsForGroupNode((XLeftNode) root, targetsForShowList);
        }
        return getShowTargetsForGroupNode(root, targetsForShowList);
    }

    private static GroupNode getShowTargetsForGroupNode(XLeftNode root, final List<ResultTarget> targetsForShowList) {
        // 从计算结果中提取要展示的结果集
        Iterator<GroupNode> iterator = new MapperIterator<GroupNode, GroupNode>(new BFTGroupNodeIterator(root), new Function<GroupNode, GroupNode>() {
            @Override
            public GroupNode apply(GroupNode p) {
                List<AggregatorValue[]> allValues = ((XLeftNode) p).getValueArrayList();
                List<AggregatorValue[]> showValues = new ArrayList<AggregatorValue[]>();
                for (int i = 0; i < allValues.size(); i++) {
                    showValues.add(new AggregatorValue[targetsForShowList.size()]);
                }
                for (int i = 0; i < allValues.size(); i++) {
                    AggregatorValue[] values = allValues.get(i);
                    for (int j = 0; j < values.length; j++) {
                        showValues.get(i)[j] = allValues.get(i)[targetsForShowList.get(j).getResultFetchIndex()];
                    }
                }
                return p;
            }
        });
        while (iterator.hasNext()) {
            iterator.next();
        }
        return root;
    }

    private static GroupNode getShowTargetsForGroupNode(GroupNode root, final List<ResultTarget> targetsForShowList) {
        // 从计算结果中提取要展示的结果集
        Iterator<GroupNode> iterator = new MapperIterator<GroupNode, GroupNode>(new BFTGroupNodeIterator(root), new Function<GroupNode, GroupNode>() {
            @Override
            public GroupNode apply(GroupNode p) {
                AggregatorValue[] showValues = new AggregatorValue[targetsForShowList.size()];
                AggregatorValue[] allValues = p.getAggregatorValue();
                for (int i = 0; i < showValues.length; i++) {
                    showValues[i] = allValues[targetsForShowList.get(i).getResultFetchIndex()];
                }
                p.setAggregatorValue(showValues);
                return p;
            }
        });
        while (iterator.hasNext()) {
            iterator.next();
        }
        return root;
    }
}
