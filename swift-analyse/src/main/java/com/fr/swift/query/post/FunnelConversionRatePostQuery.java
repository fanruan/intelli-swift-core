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
public class FunnelConversionRatePostQuery implements Query<QueryResultSet<SwiftNode>> {

    private Query<QueryResultSet<SwiftNode>> postQuery;

    public FunnelConversionRatePostQuery(Query<QueryResultSet<SwiftNode>> postQuery) {
        this.postQuery = postQuery;
    }

    @Override
    public QueryResultSet<SwiftNode> getQueryResult() throws SQLException {
        SwiftNodeOperator operator = new SwiftNodeOperator() {
            @Override
            public SwiftNode apply(SwiftNode p) {
                GroupNode node = new GroupNode(p.getDepth(), p.getData());
                if (p.getChildrenSize() > 0) {
                    for (SwiftNode child : p.getChildren()) {
                        node.addChild(apply(child));
                    }
                } else {
                    List<AggregatorValue> aggregatorValues = new ArrayList<AggregatorValue>();
                    List<AggregatorValue> postAggregatorValues = new ArrayList<AggregatorValue>();
                    for (AggregatorValue value : p.getAggregatorValue()) {
                        aggregatorValues.add(value);
                        if (value instanceof FunnelAggregatorValue) {
                            FunnelAggregatorValue funnelValue = (FunnelAggregatorValue) value;
                            for (Map.Entry<FunnelGroupKey, FunnelAggValue> entry : funnelValue.getValueMap().entrySet()) {
                                int[] count = entry.getValue().getCount();
                                for (int i = 0; i < count.length - 1; i++) {
                                    if (count[i] == 0) {
                                        postAggregatorValues.add(new DoubleAmountAggregatorValue(Double.NaN));
                                        continue;
                                    }
                                    postAggregatorValues.add(new DoubleAmountAggregatorValue(100 * ((double) count[i + 1]) / count[i]));
                                }
                            }
                        }
                    }
                    aggregatorValues.addAll(postAggregatorValues);
                    node.setAggregatorValue(aggregatorValues.toArray(new AggregatorValue[0]));
                }
                return node;
            }
        };
        return new ChainedNodeQueryResultSet(operator, postQuery.getQueryResult());
    }
}
