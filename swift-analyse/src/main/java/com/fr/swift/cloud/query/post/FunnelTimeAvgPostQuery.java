package com.fr.swift.cloud.query.post;

import com.fr.swift.cloud.query.aggregator.AggregatorValue;
import com.fr.swift.cloud.query.aggregator.DoubleAmountAggregatorValue;
import com.fr.swift.cloud.query.aggregator.FunnelAggregatorValue;
import com.fr.swift.cloud.query.aggregator.FunnelHelperValue;
import com.fr.swift.cloud.query.group.FunnelGroupKey;
import com.fr.swift.cloud.query.query.Query;
import com.fr.swift.cloud.result.GroupNode;
import com.fr.swift.cloud.result.SwiftNode;
import com.fr.swift.cloud.result.SwiftNodeOperator;
import com.fr.swift.cloud.result.node.resultset.ChainedNodeQueryResultSet;
import com.fr.swift.cloud.result.qrs.QueryResultSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2018/12/13
 *
 * @author yee
 * @description
 */
public class FunnelTimeAvgPostQuery implements Query<QueryResultSet<SwiftNode>> {

    private Query<QueryResultSet<SwiftNode>> postQuery;

    public FunnelTimeAvgPostQuery(Query<QueryResultSet<SwiftNode>> postQuery) {
        this.postQuery = postQuery;
    }

    @Override
    public QueryResultSet<SwiftNode> getQueryResult() throws SQLException {

        SwiftNodeOperator operator = new SwiftNodeOperator() {
            @Override
            public SwiftNode apply(SwiftNode node) {
                GroupNode current = new GroupNode(node.getDepth(), node.getData());
                if (node.getChildrenSize() > 0) {
                    for (SwiftNode child : node.getChildren()) {
                        current.addChild(apply(child));
                    }
                } else {
                    List<AggregatorValue> aggregatorValues = new ArrayList<AggregatorValue>();
                    List<AggregatorValue> postAggregatorValues = new ArrayList<AggregatorValue>();
                    for (AggregatorValue value : node.getAggregatorValue()) {
                        aggregatorValues.add(value);
                        if (value instanceof FunnelAggregatorValue) {
                            FunnelAggregatorValue funnelValue = (FunnelAggregatorValue) value;
                            for (Map.Entry<FunnelGroupKey, FunnelHelperValue> entry : funnelValue.getValueMap().entrySet()) {
                                List<List<Long>> periods = entry.getValue().getPeriods();
                                for (int i = 0; i < periods.size(); i++) {
                                    List<Long> list = periods.get(i);
                                    if (list.isEmpty()) {
                                        postAggregatorValues.add(new DoubleAmountAggregatorValue(Double.NaN));
                                        continue;
                                    }
                                    postAggregatorValues.add(new DoubleAmountAggregatorValue(calAvg(list)));
                                }
                            }
                        }
                    }
                    aggregatorValues.addAll(postAggregatorValues);
                    current.setAggregatorValue(aggregatorValues.toArray(new AggregatorValue[0]));
                }
                return current;
            }
        };
        return new ChainedNodeQueryResultSet(operator, postQuery.getQueryResult());
    }

    private double calAvg(List<Long> list) {
        double sum = 0D;
        for (Long aLong : list) {
            sum += aLong;
        }
        return sum / list.size();
    }
}
