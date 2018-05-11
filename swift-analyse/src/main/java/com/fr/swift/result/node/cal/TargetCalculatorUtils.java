package com.fr.swift.result.node.cal;

import com.fr.general.ComparatorUtils;
import com.fr.swift.query.adapter.target.GroupTarget;
import com.fr.swift.query.adapter.target.cal.ResultTarget;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.TopGroupNode;
import com.fr.swift.result.XLeftNode;
import com.fr.swift.result.node.iterator.BFTGroupNodeIterator;
import com.fr.swift.structure.iterator.MapperIterator;
import com.fr.swift.util.function.Function;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/4/8.
 */
public class TargetCalculatorUtils {

    /**
     * 对node进行指标的配置类计算，并从中间计算结果指中取出最终要显示的指标
     *
     * @param root 根节点
     *  infoList 用于配置类计算的属性
     *  targetsForShowList 最终要返回的一组指标的位置&顺序属性
     * @return 返回处理完计算指标，并去除配置计算产生的中间结果指标的node根节点
     * @throws SQLException
     */
    public static GroupNode calculate(GroupNode root, List<GroupTarget> groupTargets) throws SQLException {
        if (groupTargets.size() == 0) {
            return root;
        }
        List<TargetCalculator> calculators = new ArrayList<TargetCalculator>();
        for (int i = 0; i < groupTargets.size(); i++) {
            calculators.add(TargetCalculatorFactory.create(groupTargets.get(i), root));
        }
        for (TargetCalculator calculator : calculators) {
            try {
                calculator.call();
            } catch (Exception e) {
                throw new SQLException(e);
            }
        }
        return root;
    }

    public static void setTopGroupNodeData(TopGroupNode root, final List<Map<Integer, Object>> dictionaries) {
        Iterator<GroupNode> iterator = new MapperIterator<GroupNode, GroupNode>(new BFTGroupNodeIterator(root), new Function<GroupNode, GroupNode>() {
            @Override
            public GroupNode apply(GroupNode p) {
                if (p.getDepth() != -1) {
                    p.setData(dictionaries.get(p.getDepth()).get(p.getDictionaryIndex()));
                }
                return p;
            }
        });
        while (iterator.hasNext()) {
            iterator.next();
        }
    }

    public static void getShowTargetsForXLeftNodeAndSetNodeDataAndSetNodeIndex(XLeftNode root,
                                                                               final List<ResultTarget> targetsForShowList,
                                                                               final List<Map<Integer, Object>> dictionaries) {
        final IndexCounter indexCounter = new IndexCounter();
        // 从计算结果中提取要展示的结果集
        Iterator<GroupNode> iterator = new MapperIterator<GroupNode, GroupNode>(new BFTGroupNodeIterator(root), new Function<GroupNode, GroupNode>() {
            @Override
            public GroupNode apply(GroupNode p) {
                // 设置节点的data
                if (p.getDepth() != -1) {
                    p.setData(dictionaries.get(p.getDepth()).get(p.getDictionaryIndex()));
                }
                // 设置节点的index，这个比较猥琐了
                p.setIndex(indexCounter.index(p));
                List<AggregatorValue[]> allValues = ((XLeftNode) p).getValueArrayList();
                if (allValues == null) {
                    // 非叶子节点可能为空
                    ((XLeftNode) p).setValueArrayList(new ArrayList<AggregatorValue[]>(0));
                    return p;
                }
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
    }

    public static void getShowTargetsForGroupNodeAndSetNodeDataAndSetNodeIndex(GroupNode root,
                                                                               final List<ResultTarget> targetsForShowList,
                                                                               final List<Map<Integer, Object>> dictionaries) {
        final IndexCounter indexCounter = new IndexCounter();
        // 从计算结果中提取要展示的结果集
        Iterator<GroupNode> iterator = new MapperIterator<GroupNode, GroupNode>(new BFTGroupNodeIterator(root), new Function<GroupNode, GroupNode>() {
            @Override
            public GroupNode apply(GroupNode p) {
                AggregatorValue[] showValues = new AggregatorValue[targetsForShowList.size()];
                AggregatorValue[] allValues = p.getAggregatorValue();
                for (int i = 0; i < showValues.length; i++) {
                    if (allValues.length == 0) {
                        // TODO: 2018/4/28 父节点汇总为空问题
                        break;
                    }
                    showValues[i] = allValues[targetsForShowList.get(i).getResultFetchIndex()];
                }
                p.setAggregatorValue(showValues);
                // 设置节点的data
                if (p.getDepth() != -1) {
                    p.setData(dictionaries.get(p.getDepth()).get(p.getDictionaryIndex()));
                }
                // 设置节点的index，这个比较猥琐了
                p.setIndex(indexCounter.index(p));
                return p;
            }
        });
        while (iterator.hasNext()) {
            iterator.next();
        }
    }

    public static void updateNodeIndexAfterSort(GroupNode root) {
        final IndexCounter indexCounter = new IndexCounter();
        Iterator<GroupNode> iterator = new MapperIterator<GroupNode, GroupNode>(new BFTGroupNodeIterator(root), new Function<GroupNode, GroupNode>() {
            @Override
            public GroupNode apply(GroupNode p) {
                p.setIndex(indexCounter.index(p));
                return null;
            }
        });
        while (iterator.hasNext()) {
            iterator.next();
        }
    }

    private static class IndexCounter {
        private Object parent = null;
        private int siblingCounter = 0;

        public int index(GroupNode node) {
            if (node.getParent() == null) {
                // 根节点
                return -1;
            }
            if (ComparatorUtils.equals(parent, node.getParent().getData())) {
                // 兄弟节点
                return siblingCounter++;
            }
            // BFTGroupNodeIterator迭代器的维度切换了
            parent = node.getParent().getData();
            siblingCounter = 0;
            return siblingCounter++;
        }
    }
}
