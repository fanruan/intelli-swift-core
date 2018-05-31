package com.fr.swift.query.post.group;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.filter.FilterBuilder;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.match.MatchFilter;
import com.fr.swift.query.filter.match.NodeSorter;
import com.fr.swift.query.info.XGroupQueryInfo;
import com.fr.swift.query.info.dimension.Dimension;
import com.fr.swift.query.post.AbstractPostQuery;
import com.fr.swift.query.result.ResultQuery;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.TopGroupNode;
import com.fr.swift.result.XLeftNode;
import com.fr.swift.result.XNodeMergeResultSet;
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
public class XGroupPostQuery extends AbstractPostQuery<NodeResultSet> {

    private ResultQuery<NodeResultSet> mergeQuery;
    private XGroupQueryInfo info;

    public XGroupPostQuery(ResultQuery<NodeResultSet> mergeQuery,
                           XGroupQueryInfo queryInfo) {
        this.mergeQuery = mergeQuery;
        this.info = queryInfo;
    }

    @Override
    public NodeResultSet getQueryResult() throws SQLException {
        XNodeMergeResultSet resultSet = (XNodeMergeResultSet) mergeQuery.getQueryResult();
        // 下面设置字典、取出要返回的结果指标、对结果指标做横向和列向汇总、结果指标转为二维数组
//        int rowDimensionSize = info.getDimensionInfo().getDimensions().length;
//        int colDimensionSize = info.getColDimensionInfo().getDimensions().length;
//        GroupNodeUtils.updateNodeData(colDimensionSize, resultSet.getTopGroupNode(), resultSet.getColGlobalDictionaries());
//        GroupNodeUtils.updateNodeData(rowDimensionSize, (XLeftNode) resultSet.getNode(), resultSet.getRowGlobalDictionaries());
//
//        // 在明细的基础上对Node做合计，用于过滤
//        GroupNodeAggregateUtils.aggregate(NodeType.X_LEFT, rowDimensionSize,
//                (GroupNode) resultSet.getNode(), resultSet.getAggregators());
//        // 先更新topGroupNode里面的topGroupValues，然后在做列向汇总。为什么呢？因为要对xLeftNode横向的汇总行做列向汇总
//        // TODO: 2018/5/23 列表头要支持对指标的过滤实在太麻烦了，能不支持就不不支持吧
//        XNodeUtils.updateTopGroupNodeValues(colDimensionSize, rowDimensionSize,
//                resultSet.getTopGroupNode(), (XLeftNode) resultSet.getNode());
//        GroupNodeAggregateUtils.aggregate(NodeType.TOP_GROUP, colDimensionSize,
//                resultSet.getTopGroupNode(), resultSet.getAggregators());
////        // 先更新一下xLeftNode#valueArrayList（包含topGroupNode所有列（包括汇总列）的某一行）
//        updateXLeftNode(rowDimensionSize, colDimensionSize, resultSet);
//
//        // 指标排序，处理的思路是利用横向和纵向汇总的得到的根节点汇总值，转为两个分组表的排序来处理
//        // 暂时先这么实现，后面再分析一下能不能优化
//        if (GroupPostQuery.hasDimensionTargetSorts(info.getDimensionInfo().getDimensions())) {
//            // 行表头排序
//            sortXLeftNode(rowDimensionSize, colDimensionSize, resultSet);
//            GroupNodeUtils.updateNodeIndexAfterSort((GroupNode) resultSet.getNode());
//        }
//        if (GroupPostQuery.hasDimensionTargetSorts(info.getColDimensionInfo().getDimensions())) {
//            // TODO: 2018/5/23 列表头的排序同样比较麻烦，有的快速计算要放到排序之前
//            // 列表头排序
//            sortTopGroupNode(rowDimensionSize, colDimensionSize, resultSet);
//            GroupNodeUtils.updateNodeIndexAfterSort((GroupNode) resultSet.getNode());
//        }
//
//        // 处理计算指标
//        TargetCalculatorUtils.calculate(((GroupNode) resultSet.getNode()),
//                resultSet.getRowGlobalDictionaries(), info.getTargetInfo().getGroupTargets());
//
//        // 结果过滤
//        List<MatchFilter> dimensionMatchFilter = getDimensionMatchFilters(info.getDimensionInfo().getDimensions());
//        if (hasDimensionFilter(dimensionMatchFilter)) {
//            NodeFilter.filter(resultSet.getNode(), dimensionMatchFilter);
//        }
//
//        // 二次计算
//        TargetCalculatorUtils.calculateAfterFiltering(((GroupNode) resultSet.getNode()),
//                resultSet.getRowGlobalDictionaries(), info.getTargetInfo().getGroupTargets());
//
//        // 取出要展示的指标，再做合计
//        GroupNodeUtils.updateShowTargetsForXLeftNode(rowDimensionSize, (XLeftNode) resultSet.getNode(),
//                info.getTargetInfo().getTargetsForShowList());
//        List<Aggregator> aggregators = info.getTargetInfo().getResultAggregators();
//        GroupNodeAggregateUtils.aggregate(NodeType.X_LEFT, rowDimensionSize, (GroupNode) resultSet.getNode(), aggregators);
//        // 先更新topGroupNode里面的topGroupValues，然后在做列向汇总
//        XNodeUtils.updateTopGroupNodeValues(colDimensionSize, rowDimensionSize,
//                resultSet.getTopGroupNode(), (XLeftNode) resultSet.getNode());
//        GroupNodeAggregateUtils.aggregate(NodeType.TOP_GROUP, colDimensionSize, resultSet.getTopGroupNode(), aggregators);
//        // 再更新一下xLeftNode#valueArrayList（包含topGroupNode所有列（包括汇总列）的某一行）
//        updateXLeftNode(rowDimensionSize, colDimensionSize, resultSet);
//
//        if (isEmpty(resultSet)) {
//            return resultSet;
//        }
//        // 最后一步将xLeftNode的List<AggregatorValue[]> valueArrayList转为二维数组
//        if (GroupPostQuery.hasDimensionTargetSorts(info.getDimensionInfo().getDimensions())
//                || GroupPostQuery.hasDimensionTargetSorts(info.getColDimensionInfo().getDimensions())) {
//            // 从排序结果中处理XLeftNode的最后结果xValues数组
//            setValues2XLeftNodeFromSortResult(rowDimensionSize, colDimensionSize, resultSet);
//        } else {
//            XNodeUtils.setValues2XLeftNode(info.getColDimensionInfo().isShowSum(), colDimensionSize, rowDimensionSize,
//                    resultSet.getTopGroupNode(), (XLeftNode) resultSet.getNode());
//        }
        return resultSet;
    }


    private static List<MatchFilter> getDimensionMatchFilters(Dimension[] dimensions) {
        List<MatchFilter> matchFilters = new ArrayList<MatchFilter>(dimensions.length);
        for (Dimension dimension : dimensions) {
            FilterInfo filter = null;
            if (filter != null && filter.isMatchFilter()) {
                matchFilters.add(FilterBuilder.buildMatchFilter(filter));
            } else {
                // 其他情况用null占位，表示不过滤
                matchFilters.add(null);
            }
        }
        return matchFilters;
    }

    private boolean hasDimensionFilter(List<MatchFilter> dimensionMatchFilter) {
        if (dimensionMatchFilter == null) {
            return false;
        }
        for (int i = 0; i < dimensionMatchFilter.size(); i++) {
            if (dimensionMatchFilter.get(i) != null) {
                return true;
            }
        }
        return false;
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
        // 这个挖掘需要这个数据进行二次处理，暂时不设置为null，应该不影响主要逻辑
        // 清理TopGroupNode中的topGroupNodeValues
        // Iterator<GroupNode> topNodes = new BFTGroupNodeIterator(resultSet.getTopGroupNode());
        // while (iterator.hasNext()) {
        //     ((TopGroupNode) topNodes.next()).setTopGroupValues(null);
        // }
    }

    private void sortTopGroupNode(int rowDimensionSize, int colDimensionSize, XNodeMergeResultSet resultSet) {
        List<Sort> colSorts = GroupPostQuery.getDimensionTargetSorts(info.getDimensions());
        // 这边XLeftNode根节点的汇总值已经包括了所有列向节点的汇总值
        List<AggregatorValue[]> values = ((XLeftNode) resultSet.getNode()).getValueArrayList();
        setSumValues2Node(colDimensionSize, resultSet.getTopGroupNode(), values);
        NodeSorter.sort(resultSet.getTopGroupNode(), colSorts);
        // 再更新一下xLeftNode#valueArrayList（包含topGroupNode所有列的某一行）
        updateXLeftNode(rowDimensionSize, colDimensionSize, resultSet);
    }

    private void sortXLeftNode(int rowDimensionSize, int colDimensionSize, XNodeMergeResultSet resultSet) {
        List<Sort> rowSorts = GroupPostQuery.getDimensionTargetSorts(info.getDimensions());
        // 这边topGroupNode的根节点的汇总值已经包括了所有横向节点的汇总值
        List<AggregatorValue[]> values = resultSet.getTopGroupNode().getTopGroupValues();
        setSumValues2Node(rowDimensionSize, (GroupNode) resultSet.getNode(), values);
        NodeSorter.sort(resultSet.getNode(), rowSorts);
        // 再更新一下topGroupNode#topGroupValues（包含xLeftNode所有行的某一列）
        updateTopGroupValues(rowDimensionSize, colDimensionSize, resultSet);
    }

    public static void updateXLeftNode(int rowDimensionSize, int colDimensionSize, XNodeMergeResultSet resultSet) {
        List<TopGroupNode> topGroupNodes = IteratorUtils.iterator2List(new PostOrderNodeIterator<TopGroupNode>(colDimensionSize, resultSet.getTopGroupNode()));
        List<XLeftNode> xLeftNodes = IteratorUtils.iterator2List(new PostOrderNodeIterator<XLeftNode>(rowDimensionSize, (XLeftNode) resultSet.getNode()));
        for (int row = 0; row < xLeftNodes.size(); row++) {
            List<AggregatorValue[]> rowValues = new ArrayList<AggregatorValue[]>();
            for (int col = 0; col < topGroupNodes.size(); col++) {
                rowValues.add(topGroupNodes.get(col).getTopGroupValues().get(row));
            }
            // 每个XLeftNode包含了列向汇总行
            xLeftNodes.get(row).setValueArrayList(rowValues);
        }
    }

    private static void updateTopGroupValues(int rowDimensionSize, int colDimensionSize, XNodeMergeResultSet resultSet) {
        List<TopGroupNode> topGroupNodes = IteratorUtils.iterator2List(new PostOrderNodeIterator<TopGroupNode>(colDimensionSize, resultSet.getTopGroupNode()));
        List<XLeftNode> xLeftNodes = IteratorUtils.iterator2List(new PostOrderNodeIterator<XLeftNode>(rowDimensionSize, (XLeftNode) resultSet.getNode()));
        for (int col = 0; col < topGroupNodes.size(); col++) {
            List<AggregatorValue[]> colValues = new ArrayList<AggregatorValue[]>();
            for (int row = 0; row < xLeftNodes.size(); row++) {
                colValues.add(xLeftNodes.get(row).getValueArrayList().get(col));
            }
            topGroupNodes.get(col).setTopGroupValues(colValues);
        }
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
