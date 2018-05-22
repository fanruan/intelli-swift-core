package com.fr.swift.result.node.xnode;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.group.by2.node.XNodeGroupByUtils;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.TopGroupNode;
import com.fr.swift.result.XLeftNode;
import com.fr.swift.result.node.iterator.BFTGroupNodeIterator;
import com.fr.swift.result.node.iterator.PostOrderNodeIterator;
import com.fr.swift.structure.iterator.Filter;
import com.fr.swift.structure.iterator.FilteredIterator;
import com.fr.swift.structure.iterator.IteratorUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 2018/4/9.
 */
public class XNodeUtils {

    /**
     * 将交叉表的结果集转为功能
     * @param isColShowSum 是否显示列向汇总值
     * @param topDimensionSize 表头维度个数
     * @param rowDimensionSize 行维度个数
     * @param topGroupNode 表头GroupBy结果
     * @param xLeftNode 行GroupBy结果
     */
    public static void setValues2XLeftNode(boolean isColShowSum, int topDimensionSize, int rowDimensionSize,
                                           TopGroupNode topGroupNode, XLeftNode xLeftNode) {
        Iterator<TopGroupNode> topIt = new PostOrderNodeIterator<TopGroupNode>(topDimensionSize, topGroupNode);

        // topGroupNode的所有行，排除了不需要显示的汇总行
        if (isColShowSum) {
            // 显示列向汇总
            topIt = excludeNoShowSummaryRow(topIt);
        } else {
            // 不显示列现汇总
            topIt = excludeAllSummaryRow(topIt);
        }
        List<TopGroupNode> topGroupNodeList = IteratorUtils.iterator2List(topIt);

        Iterator<XLeftNode> xLeftIt = new PostOrderNodeIterator<XLeftNode>(rowDimensionSize, xLeftNode);
        // xLeftNode的所有行，包括了不需要显示的汇总行
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
        // 这个挖掘需要这个数据进行二次处理，暂时不设置为null，应该不影响主要逻辑
        // 清理TopGroupNode中的topGroupNodeValues
        // Iterator<GroupNode> iterator = new BFTGroupNodeIterator(topGroupNode);
        // while (iterator.hasNext()) {
        //     ((TopGroupNode) iterator.next()).setTopGroupValues(null);
        // }
    }

    /**
     * 更新TopGroupNode#topGroupValues，包含横向的汇总行，用于做列向汇总
     *
     * @param topDimensionSize 表头维度个数
     * @param rowDimensionSize 行维度个数
     * @param topGroupNode 表头GroupBy结果
     * @param xLeftNode 行GroupBy结果
     */
    public static void updateTopGroupNodeValues(int topDimensionSize, int rowDimensionSize,
                                                TopGroupNode topGroupNode, XLeftNode xLeftNode) {
        // 这边包括了不需要显示汇总行的XLeftNode
        Iterator<XLeftNode> xLeftIt = new PostOrderNodeIterator<XLeftNode>(rowDimensionSize, xLeftNode);
        List<XLeftNode> xLeftNodeList = IteratorUtils.iterator2List(xLeftIt);
        // topGroupNode的子节点
        List<TopGroupNode> topGroupNodeList = XNodeGroupByUtils.getLeafNodes(topDimensionSize, topGroupNode);
        XNodeGroupByUtils.updateValues2TopGroupNode(xLeftNodeList, topGroupNodeList);
    }

    public static <N extends GroupNode> Iterator<N> excludeNoShowSummaryRow(Iterator<N> iterator) {
        return new FilteredIterator<N>(iterator, new Filter<N>() {
            @Override
            public boolean accept(N n) {
                // 过滤掉不用显示的汇总行
                return n.getChildrenSize() != 1;
            }
        });
    }

    public static <N extends GroupNode> Iterator<N> excludeAllSummaryRow(Iterator<N> iterator) {
        return new FilteredIterator<N>(iterator, new Filter<N>() {
            @Override
            public boolean accept(N n) {
                // 过滤掉不用显示的汇总行
                return n.getChildrenSize() == 0;
            }
        });
    }
}
