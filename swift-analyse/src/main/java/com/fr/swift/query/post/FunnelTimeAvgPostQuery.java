package com.fr.swift.query.post;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.aggregator.DoubleAmountAggregatorValue;
import com.fr.swift.query.aggregator.FunnelAggValue;
import com.fr.swift.query.aggregator.FunnelAggregatorValue;
import com.fr.swift.query.group.FunnelGroupKey;
import com.fr.swift.query.query.Query;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.SwiftNodeOperator;
import com.fr.swift.result.node.resultset.ChainedNodeQueryResultSet;
import com.fr.swift.result.qrs.QueryResultSet;

import java.math.BigDecimal;
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
            public SwiftNode apply(SwiftNode p) {
                GroupNode node = new GroupNode(p.getDepth(), p.getData());
                for (SwiftNode next : p.getChildren()) {
                    List<AggregatorValue> aggregatorValues = new ArrayList<AggregatorValue>();
                    List<AggregatorValue> postAggregatorValues = new ArrayList<AggregatorValue>();
                    for (AggregatorValue value : next.getAggregatorValue()) {
                        aggregatorValues.add(value);
                        if (value instanceof FunnelAggregatorValue) {
                            FunnelAggregatorValue funnelValue = (FunnelAggregatorValue) value;
                            for (Map.Entry<FunnelGroupKey, FunnelAggValue> entry : funnelValue.getValueMap().entrySet()) {
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
                    next.setAggregatorValue(aggregatorValues.toArray(new AggregatorValue[0]));
                    node.addChild(next);
                }
                return node;
            }
        };
        return new ChainedNodeQueryResultSet(operator, postQuery.getQueryResult());
    }

    private double calAvg(List<Long> list) {
        BigDecimal sum = new BigDecimal(0L);
        for (Long aLong : list) {
            sum.add(new BigDecimal(aLong));
        }
        return sum.divide(new BigDecimal(list.size())).doubleValue();
    }
}
