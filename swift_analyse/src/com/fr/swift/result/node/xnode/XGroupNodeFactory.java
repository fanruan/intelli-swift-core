package com.fr.swift.result.node.xnode;

import com.fr.swift.query.adapter.target.cal.TargetInfo;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.structure.iterator.Filter;
import com.fr.swift.structure.iterator.FilteredIterator;
import com.fr.swift.structure.iterator.MapperIterator;
import com.fr.swift.result.KeyValue;
import com.fr.swift.result.RowIndexKey;
import com.fr.swift.result.XGroupByResultSet;
import com.fr.swift.result.node.GroupNode;
import com.fr.swift.result.node.GroupNodeAggregateUtils;
import com.fr.swift.result.node.GroupNodeFactory;
import com.fr.swift.result.node.NodeType;
import com.fr.swift.result.node.cal.TargetCalculatorUtils;
import com.fr.swift.structure.iterator.IteratorUtils;
import com.fr.swift.result.node.iterator.PostOrderNodeIterator;
import com.fr.swift.util.function.Function;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/4/9.
 */
public class XGroupNodeFactory {

    /**
     * 构建交叉表node，并且处理计算指标
     *
     * @param resultSet 交叉表聚合的行结果集
     * @param targetInfo 交叉表计算指标相关属性
     * @return 交叉表计算结果的node根节点
     * @throws Exception
     */
    public static XGroupNode createXGroupNode(XGroupByResultSet resultSet, TargetInfo targetInfo) throws Exception {
        // 构建节点
        XLeftNode xLeftNode = XLeftNodeFactory.createXLeftNode(resultSet, targetInfo.getTargetLength());
        // 处理计算指标
//        xLeftNode = (XLeftNode) TargetCalculatorUtils.calculate(xLeftNode,
//                targetInfo.getTargetCalculatorInfoList(), targetInfo.getTargetsForShowList());
        GroupNodeAggregateUtils.aggregate(NodeType.X_LEFT, resultSet.rowDimensionSize(), xLeftNode,
                targetInfo.getAggregatorListOfTargetsForShow());
        // 表头groupBy的维度key
        List<RowIndexKey<int[]>> keys = getTopDimensionKeys(resultSet);
        // 表头groupBy的行结果集
        List<KeyValue<RowIndexKey<int[]>, List<AggregatorValue[]>>> topResultSet = getTopGroupResultSet(
                resultSet.rowDimensionSize(), xLeftNode, keys);
        // 根据表头的行结果集构建TopGroupNode
        TopGroupNode topGroupNode = createTopGroupNode(0, resultSet.colDimensionSize(),
                topResultSet, resultSet.getColGlobalDictionaries());
        // 通过topGroupNode来做列向汇总
        GroupNodeAggregateUtils.aggregate(NodeType.TOP_GROUP, resultSet.colDimensionSize(), topGroupNode,
                targetInfo.getAggregatorListOfTargetsForShow());
        // 给xLeftNode重新设置value，增加了列向汇总值
        setValues2XLeftNode(resultSet.colDimensionSize(), resultSet.rowDimensionSize(), topGroupNode, xLeftNode);
        // TODO: 2018/4/11 topGroupNode是不是要清理一下引用呢？
        return new XGroupNodeImpl(xLeftNode, topGroupNode);
    }

    private static void setValues2XLeftNode(int topDimensionSize, int rowDimensionSize,
                                                 TopGroupNode topGroupNode, XLeftNode xLeftNode) {
        Iterator<TopGroupNode> topIt = new PostOrderNodeIterator<TopGroupNode>(topDimensionSize, topGroupNode);
        topIt = excludeNoShowSummaryRow(topIt);
        // topGroupNode的所有行，排除了不需要显示的汇总行
        List<TopGroupNode> topGroupNodeList = IteratorUtils.iterator2List(topIt);

        Iterator<XLeftNode> xLeftIt = new PostOrderNodeIterator<XLeftNode>(rowDimensionSize, xLeftNode);
        xLeftIt = excludeNoShowSummaryRow(xLeftIt);
        // xLeftNode的所有行，排除了不需要显示的汇总行
        List<XLeftNode> xLeftNodeList = IteratorUtils.iterator2List(xLeftIt);

        // 遍历一遍二维数组
        for (int row = 0; row < xLeftNodeList.size(); row++) {
            List<AggregatorValue[]> rowValues = new ArrayList<AggregatorValue[]>();
            for (int col = 0; col < topGroupNodeList.size(); col++) {
                List<AggregatorValue[]> colValues = topGroupNodeList.get(col).getTopGroupValues();
                // colValues对应xLeftNode在对应列所有行的值
                assert colValues.size() == xLeftNodeList.size();
                // 取出xLeftNode在坐标(row, col)的值
                rowValues.add(colValues.get(row));
            }
            // 给xLeftNode对应行设置值
            xLeftNodeList.get(row).setXValues(rowValues);
            // 同时清理一下保存中间结果的List
            xLeftNodeList.get(row).setValueArrayList(null);
        }
    }

    private static List<RowIndexKey<int[]>> getTopDimensionKeys(XGroupByResultSet resultSet) {
        List<KeyValue<RowIndexKey<int[]>, List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>>>> xRowList =
                resultSet.getXResultList();
        List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>> topResultSet = xRowList.isEmpty() ?
                new ArrayList<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>>() : xRowList.get(0).getValue();
        Iterator<RowIndexKey<int[]>> iterator = new MapperIterator<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>, RowIndexKey<int[]>>(topResultSet.iterator(), new Function<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>, RowIndexKey<int[]>>() {
            @Override
            public RowIndexKey<int[]> apply(KeyValue<RowIndexKey<int[]>, AggregatorValue[]> p) {
                return p.getKey();
            }
        });
        return IteratorUtils.iterator2List(iterator);
    }

    private static TopGroupNode createTopGroupNode(int targetLength, int topDimensionSize,
                                                   List<KeyValue<RowIndexKey<int[]>, List<AggregatorValue[]>>> topResultSet,
                                                   List<Map<Integer, Object>> colDictionaries) {
        GroupNodeFactory.Creator<TopGroupNode> creator = new GroupNodeFactory.Creator<TopGroupNode>() {
            @Override
            public TopGroupNode create(int deep, Object data) {
                return new TopGroupNode(deep, data);
            }
        };
        GroupNodeFactory.ValueSetter<TopGroupNode, List<AggregatorValue[]>> valueSetter = new GroupNodeFactory.ValueSetter<TopGroupNode, List<AggregatorValue[]>>() {
            @Override
            public void setValue(TopGroupNode node, List<AggregatorValue[]> aggregatorValues, int targetLength) {
                node.setTopGroupValues(aggregatorValues);
            }
        };
        return GroupNodeFactory.createFromSortedList(targetLength, topDimensionSize, topResultSet, colDictionaries,
                creator, valueSetter);
    }

    /**
     * 为了计算交叉表的列向汇总，对XLeftNode进行转化
     */
    private static List<KeyValue<RowIndexKey<int[]>, List<AggregatorValue[]>>> getTopGroupResultSet(
            int rowDimensionSize, XLeftNode xLeftNode, List<RowIndexKey<int[]>> keys) {
        List<KeyValue<RowIndexKey<int[]>, List<AggregatorValue[]>>> resultSet;
        resultSet = IteratorUtils.iterator2List(new MapperIterator<RowIndexKey<int[]>, KeyValue<RowIndexKey<int[]>, List<AggregatorValue[]>>>(keys.iterator(), new Function<RowIndexKey<int[]>, KeyValue<RowIndexKey<int[]>, List<AggregatorValue[]>>>() {
            @Override
            public KeyValue<RowIndexKey<int[]>, List<AggregatorValue[]>> apply(RowIndexKey<int[]> p) {
                return new KeyValue<RowIndexKey<int[]>, List<AggregatorValue[]>>(p, new ArrayList<AggregatorValue[]>());
            }
        }));
        Iterator<XLeftNode> iterator = new PostOrderNodeIterator<XLeftNode>(rowDimensionSize, xLeftNode);
        // xLeftNode所有行的迭代器，排除了不需要显示的汇总行
        iterator = excludeNoShowSummaryRow(iterator);
        while (iterator.hasNext()) {
            XLeftNode node = iterator.next();
            List<AggregatorValue[]> values = node.getValueArrayList();
            assert values.size() == resultSet.size();
            for (int i = 0; i < values.size(); i++) {
                resultSet.get(i).getValue().add(values.get(i));
            }
        }
        return resultSet;
    }

    private static <N extends GroupNode> Iterator<N> excludeNoShowSummaryRow(Iterator<N> iterator) {
        return new FilteredIterator<N>(iterator, new Filter<N>() {
            @Override
            public boolean accept(N n) {
                // 过滤掉不用显示的汇总行
                if (n.getChildrenSize() == 1) {
                    return false;
                }
                return true;
            }
        });
    }
}
