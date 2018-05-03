package com.fr.swift.cal.targetcal.group;

import com.fr.swift.cal.info.XGroupQueryInfo;
import com.fr.swift.cal.result.ResultQuery;
import com.fr.swift.cal.targetcal.AbstractTargetCalQuery;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.XLeftNode;
import com.fr.swift.result.XNodeMergeResultSet;
import com.fr.swift.result.node.GroupNodeAggregateUtils;
import com.fr.swift.result.node.NodeType;
import com.fr.swift.result.node.cal.TargetCalculatorUtils;
import com.fr.swift.result.node.xnode.XNodeUtils;

import java.sql.SQLException;
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
        TargetCalculatorUtils.calculate(((GroupNode) resultSet.getNode()), info.getTargetInfo().getGroupTargets());
        // TODO: 2018/5/2 结果过滤

        // 下面设置字典、取出要返回的结果指标、对结果指标做横向和列向汇总、结果指标转为二维数组
        TargetCalculatorUtils.setTopGroupNodeData(resultSet.getTopGroupNode(), resultSet.getColGlobalDictionaries());
        TargetCalculatorUtils.getShowTargetsForXLeftNodeAndSetNodeData((XLeftNode) resultSet.getNode(),
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
        // 最后一步将xLeftNode的List<AggregatorValue[]> valueArrayList转为二维数组
        XNodeUtils.setValues2XLeftNode(colDimensionSize, rowDimensionSize,
                resultSet.getTopGroupNode(), (XLeftNode) resultSet.getNode());
        return resultSet;
    }
}
