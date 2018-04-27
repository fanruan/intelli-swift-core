package com.fr.swift.cal.targetcal.group;

import com.fr.swift.cal.info.GroupQueryInfo;
import com.fr.swift.cal.result.ResultQuery;
import com.fr.swift.cal.targetcal.AbstractTargetCalQuery;
import com.fr.swift.query.adapter.dimension.Dimension;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.filter.FilterBuilder;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.match.MatchFilter;
import com.fr.swift.query.filter.match.NodeAggregator;
import com.fr.swift.query.filter.match.NodeFilter;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.node.cal.TargetCalculatorUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2018/4/26.
 */
public class GroupTargetCalQuery extends AbstractTargetCalQuery<NodeResultSet> {

    private ResultQuery<NodeResultSet> mergeQuery;
    private GroupQueryInfo info;

    public GroupTargetCalQuery(ResultQuery<NodeResultSet> mergeQuery, GroupQueryInfo info) {
        this.mergeQuery = mergeQuery;
        this.info = info;
    }

    @Override
    public NodeResultSet getQueryResult() throws SQLException {
        // 合并后的结果
        NodeResultSet mergeResult = mergeQuery.getQueryResult();
        // 根据合并后的结果处理计算指标的计算
        TargetCalculatorUtils.calculate(((GroupNode) mergeResult.getNode()), info.getTargetInfo().getGroupTargets());
        // 进行结果过滤
        List<MatchFilter> dimensionMatchFilter = getDimensionMatchFilters(info.getDimensionInfo().getDimensions());
        if (hasDimensionFilter(dimensionMatchFilter)) {
            NodeFilter.filter(mergeResult.getNode(), dimensionMatchFilter);
            List<Aggregator> aggregators = info.getTargetInfo().getAggregatorListForResultMerging();
            NodeAggregator.aggregate(mergeResult.getNode(), aggregators.toArray(new Aggregator[aggregators.size()]));
        }
        // 取出查询最后要返回的结果
        TargetCalculatorUtils.getShowTargetsForGroupNode(((GroupNode) mergeResult.getNode()),
                info.getTargetInfo().getTargetsForShowList());
        return mergeResult;
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

    private static List<MatchFilter> getDimensionMatchFilters(Dimension[] dimensions) {
        List<MatchFilter> matchFilters = new ArrayList<MatchFilter>(dimensions.length);
        for (Dimension dimension : dimensions) {
            FilterInfo filter = dimension.getFilter();
            if (filter != null && filter.isMatchFilter()) {
                matchFilters.add(FilterBuilder.buildMatchFilter(filter));
            }
        }
        return matchFilters;
    }
}
