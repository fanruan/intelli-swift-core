package com.fr.swift.result.node;

import com.fr.general.ComparatorUtils;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.info.target.cal.ResultTarget;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.XLeftNode;
import com.fr.swift.result.node.iterator.BFTGroupNodeIterator;
import com.fr.swift.result.node.iterator.DFTGroupNodeIterator;
import com.fr.swift.structure.iterator.MapperIterator;
import com.fr.swift.util.function.Function;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/5/22.
 */
public class GroupNodeUtils {

    public static void updateNodeData(int dimensionSize, GroupNode root, final List<Map<Integer, Object>> dictionaries) {
        // 从计算结果中提取要展示的结果集
        Iterator<GroupNode> iterator = new MapperIterator<GroupNode, GroupNode>(new DFTGroupNodeIterator(dimensionSize, root), new Function<GroupNode, GroupNode>() {
            @Override
            public GroupNode apply(GroupNode p) {
                // 设置节点的data
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

    public static void updateShowTargetsForXLeftNode(int rowDimensionSize, XLeftNode root,
                                                     final List<ResultTarget> targetsForShowList) {
        Iterator<GroupNode> iterator = new MapperIterator<GroupNode, GroupNode>(new DFTGroupNodeIterator(rowDimensionSize, root), new Function<GroupNode, GroupNode>() {
            @Override
            public GroupNode apply(GroupNode p) {
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
                    AggregatorValue[] forShowValues = showValues.get(i);
                    assert forShowValues.length == targetsForShowList.size();
                    for (int n = 0; n < forShowValues.length; n++) {
                        forShowValues[n] = values[targetsForShowList.get(n).getResultFetchIndex()];
                    }
                }
                ((XLeftNode) p).setValueArrayList(showValues);
                return p;
            }
        });
        while (iterator.hasNext()) {
            iterator.next();
        }
    }

    public static void updateShowTargetsForGroupNode(int dimensionSize, GroupNode root, final List<ResultTarget> targetsForShowList) {
        Iterator<GroupNode> iterator = new MapperIterator<GroupNode, GroupNode>(new DFTGroupNodeIterator(dimensionSize, root), new Function<GroupNode, GroupNode>() {
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
