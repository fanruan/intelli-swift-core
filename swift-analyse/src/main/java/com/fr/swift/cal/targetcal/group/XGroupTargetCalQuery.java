package com.fr.swift.cal.targetcal.group;

import com.fr.swift.cal.info.XGroupQueryInfo;
import com.fr.swift.cal.result.ResultQuery;
import com.fr.swift.cal.targetcal.AbstractTargetCalQuery;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.filter.match.NodeSorter;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.TopGroupNode;
import com.fr.swift.result.XLeftNode;
import com.fr.swift.result.XNodeMergeResultSet;
import com.fr.swift.result.node.GroupNodeAggregateUtils;
import com.fr.swift.result.node.NodeType;
import com.fr.swift.result.node.cal.TargetCalculatorUtils;
import com.fr.swift.result.node.iterator.BFTGroupNodeIterator;
import com.fr.swift.result.node.iterator.PostOrderNodeIterator;
import com.fr.swift.result.node.xnode.XNodeUtils;
import com.fr.swift.structure.iterator.IteratorUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 2018/4/28.
 */
public class XGroupTargetCalQuery extends AbstractTargetCalQuery<NodeResultSet> {

    private ResultQuery<NodeResultSet> mergeQuery;
    private XGroupQueryInfo info;

    public XGroupTargetCalQuery(ResultQuery<NodeResultSet> mergeQuery,
                                XGroupQueryInfo queryInfo) {
        this.mergeQuery = mergeQuery;
        this.info = queryInfo;
    }

    @Override
    public NodeResultSet getQueryResult() throws SQLException {
        XNodeMergeResultSet resultSet = (XNodeMergeResultSet) mergeQuery.getQueryResult();
        // 处理计算指标
        TargetCalculatorUtils.calculate(((GroupNode) resultSet.getNode()), resultSet.getRowGlobalDictionaries(), info.getTargetInfo().getGroupTargets());
        // TODO: 2018/5/2 结果过滤

        // 下面设置字典、取出要返回的结果指标、对结果指标做横向和列向汇总、结果指标转为二维数组
        TargetCalculatorUtils.setTopGroupNodeData(resultSet.getTopGroupNode(), resultSet.getColGlobalDictionaries());
        TargetCalculatorUtils.getShowTargetsForXLeftNodeAndSetNodeDataAndSetNodeIndex((XLeftNode) resultSet.getNode(),
                info.getTargetInfo().getTargetsForShowList(), resultSet.getRowGlobalDictionaries());
        // 对最后结果进行汇总
        int rowDimensionSize = info.getDimensionInfo().getDimensions().length;
        int colDimensionSize = info.getColDimensionInfo().getDimensions().length;
        List<Aggregator> aggregators = info.getTargetInfo().getResultAggregators();
        GroupNodeAggregateUtils.aggregate(NodeType.X_LEFT, rowDimensionSize, (GroupNode) resultSet.getNode(), aggregators);
        // 先更新topGroupNode里面的topGroupValues，然后在做列向汇总。为什么呢？因为要对xLeftNode横向的汇总行做列向汇总
        XNodeUtils.updateTopGroupNodeValues(colDimensionSize, rowDimensionSize,
                resultSet.getTopGroupNode(), (XLeftNode) resultSet.getNode());
        GroupNodeAggregateUtils.aggregate(NodeType.TOP_GROUP, colDimensionSize, resultSet.getTopGroupNode(), aggregators);

        // 指标排序，处理的思路是利用横向和纵向汇总的得到的根节点汇总值，转为两个分组表的排序来处理
        // 暂时先这么实现，后面再分析一下能不能优化
        if (GroupTargetCalQuery.hasDimensionTargetSorts(info.getDimensionInfo().getDimensions())) {
            // 指标排序，列向节点和横向节点同时进行？
            sort(rowDimensionSize, colDimensionSize, resultSet);
        }
        if (isEmpty(resultSet)) {
            return resultSet;
        }
        // 最后一步将xLeftNode的List<AggregatorValue[]> valueArrayList转为二维数组
        if (GroupTargetCalQuery.hasDimensionTargetSorts(info.getDimensionInfo().getDimensions())) {
            // 从排序结果中处理XLeftNode的最后结果xValues数组
            setValues2XLeftNodeFromSortResult(rowDimensionSize, colDimensionSize, resultSet);
        } else {
            XNodeUtils.setValues2XLeftNode(info.getColDimensionInfo().isShowSum(), colDimensionSize, rowDimensionSize,
                    resultSet.getTopGroupNode(), (XLeftNode) resultSet.getNode());
        }
        return resultSet;
    }

    private static boolean isEmpty(XNodeMergeResultSet xNodeMergeResultSet) {
        return xNodeMergeResultSet.getNode().getChildrenSize() == 0
                && xNodeMergeResultSet.getTopGroupNode().getChildrenSize() == 0;
    }

    private void setValues2XLeftNodeFromSortResult(int rowDimensionSize, int colDimensionSize, XNodeMergeResultSet resultSet) {
        // 根据排序后的结果再次更新xLeftNode
        Iterator<TopGroupNode> iterator = new PostOrderNodeIterator<TopGroupNode>(colDimensionSize, resultSet.getTopGroupNode());
        // 去掉不用显示的列向汇总行
        if (info.getColDimensionInfo().isShowSum()) {
            // 显示列向汇总
            iterator = XNodeUtils.excludeNoShowSummaryRow(iterator);
        } else {
            // 不显示列向汇总
            iterator = XNodeUtils.excludeAllSummaryRow(iterator);
        }
        List<TopGroupNode> topGroupNodes = IteratorUtils.iterator2List(iterator);
        List<XLeftNode> xLeftNodes = IteratorUtils.iterator2List(new PostOrderNodeIterator<XLeftNode>(rowDimensionSize, (XLeftNode) resultSet.getNode()));
        for (int row = 0; row < xLeftNodes.size(); row++) {
            List<AggregatorValue[]> rowValues = new ArrayList<AggregatorValue[]>();
            for (int col = 0; col < topGroupNodes.size(); col++) {
                rowValues.add(topGroupNodes.get(col).getTopGroupValues().get(row));
            }
            // 清理XLeftNode的valueArrayList
            xLeftNodes.get(row).setValueArrayList(null);
            // 设置xValues，要显示列向汇总的情况下，每个XLeftNode包含了列向汇总行
            xLeftNodes.get(row).setXValues(rowValues);
        }
        // 清理TopGroupNode中的topGroupNodeValues
        Iterator<GroupNode> topNodes = new BFTGroupNodeIterator(resultSet.getTopGroupNode());
        while (iterator.hasNext()) {
            ((TopGroupNode) topNodes.next()).setTopGroupValues(null);
        }
    }

    private void sort(int rowDimensionSize, int colDimensionSize, XNodeMergeResultSet resultSet) {
        sortXLeftNode(rowDimensionSize, colDimensionSize, resultSet);
        sortTopGroupNode(rowDimensionSize, colDimensionSize, resultSet);
    }

    private void sortTopGroupNode(int rowDimensionSize, int colDimensionSize, XNodeMergeResultSet resultSet) {
        List<Sort> colSorts = GroupTargetCalQuery.getDimensionTargetSorts(info.getColDimensionInfo().getDimensions());
        // 这边XLeftNode根节点的汇总值已经包括了所有列向节点的汇总值
        List<AggregatorValue[]> values = ((XLeftNode) resultSet.getNode()).getValueArrayList();
        setSumValues2Node(colDimensionSize, resultSet.getTopGroupNode(), values);
        List<TopGroupNode> topGroupNodes = IteratorUtils.iterator2List(new PostOrderNodeIterator<TopGroupNode>(colDimensionSize, resultSet.getTopGroupNode()));
        List<XLeftNode> xLeftNodes = IteratorUtils.iterator2List(new PostOrderNodeIterator<XLeftNode>(rowDimensionSize, (XLeftNode) resultSet.getNode()));
        // 先更新一下topGroupNode#topGroupValues（包含xLeftNode所有行的某一列）
        for (int col = 0; col < topGroupNodes.size(); col++) {
            List<AggregatorValue[]> colValues = new ArrayList<AggregatorValue[]>();
            for (int row = 0; row < xLeftNodes.size(); row++) {
                colValues.add(xLeftNodes.get(row).getValueArrayList().get(col));
            }
            topGroupNodes.get(col).setTopGroupValues(colValues);
        }
        NodeSorter.sort(resultSet.getTopGroupNode(), colSorts);
    }

    private void sortXLeftNode(int rowDimensionSize, int colDimensionSize, XNodeMergeResultSet resultSet) {
        List<Sort> rowSorts = GroupTargetCalQuery.getDimensionTargetSorts(info.getDimensionInfo().getDimensions());
        // 这边topGroupNode的根节点的汇总值已经包括了所有横向节点的汇总值
        List<AggregatorValue[]> values = resultSet.getTopGroupNode().getTopGroupValues();
        setSumValues2Node(rowDimensionSize, (GroupNode) resultSet.getNode(), values);
        List<TopGroupNode> topGroupNodes = IteratorUtils.iterator2List(new PostOrderNodeIterator<TopGroupNode>(colDimensionSize, resultSet.getTopGroupNode()));
        List<XLeftNode> xLeftNodes = IteratorUtils.iterator2List(new PostOrderNodeIterator<XLeftNode>(rowDimensionSize, (XLeftNode) resultSet.getNode()));
        // 先更新一下xLeftNode#valueArrayList（包含topGroupNode所有列的某一行）
        for (int row = 0; row < xLeftNodes.size(); row++) {
            List<AggregatorValue[]> rowValues = new ArrayList<AggregatorValue[]>();
            for (int col = 0; col < topGroupNodes.size(); col++) {
                rowValues.add(topGroupNodes.get(col).getTopGroupValues().get(row));
            }
            // 每个XLeftNode包含了列向汇总行
            xLeftNodes.get(row).setValueArrayList(rowValues);
        }
        NodeSorter.sort(resultSet.getNode(), rowSorts);
    }

    /**
     * 纵向汇总行的根节点对应xLeftNode每一行的汇总
     * xLeftNode的根节点对应topGroupNode每一行的汇总
     */
    private static void setSumValues2Node(int dimensionSize, GroupNode root, List<AggregatorValue[]> values) {
        List<GroupNode> nodes = IteratorUtils.iterator2List(new PostOrderNodeIterator<GroupNode>(dimensionSize, root));
        assert nodes.size() == values.size();
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).setAggregatorValue(values.get(i));
        }
    }
}
