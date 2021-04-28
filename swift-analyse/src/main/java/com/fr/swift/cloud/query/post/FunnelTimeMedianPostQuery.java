package com.fr.swift.cloud.query.post;

import com.fr.swift.cloud.query.aggregator.AggregatorValue;
import com.fr.swift.cloud.query.aggregator.DoubleAmountAggregatorValue;
import com.fr.swift.cloud.query.aggregator.FunnelAggregatorValue;
import com.fr.swift.cloud.query.aggregator.FunnelHelperValue;
import com.fr.swift.cloud.query.group.FunnelGroupKey;
import com.fr.swift.cloud.query.query.Query;
import com.fr.swift.cloud.query.query.funnel.TimeWindowBean;
import com.fr.swift.cloud.result.GroupNode;
import com.fr.swift.cloud.result.SwiftNode;
import com.fr.swift.cloud.result.SwiftNodeOperator;
import com.fr.swift.cloud.result.node.resultset.ChainedNodeQueryResultSet;
import com.fr.swift.cloud.result.qrs.QueryResultSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 算时间中位数
 * 这边只能转化成秒来算，毫秒精度太细数组元素很多并且容易oom
 * 另外如果用Map之类的容器效率很差
 * @author yee
 * @description
 */
public class FunnelTimeMedianPostQuery implements Query<QueryResultSet<SwiftNode>> {

    private int timeWindow;
    private Query<QueryResultSet<SwiftNode>> postQuery;

    public FunnelTimeMedianPostQuery(Query<QueryResultSet<SwiftNode>> postQuery, TimeWindowBean timeWindowBean) {
        this.timeWindow = (int) TimeUnit.MILLISECONDS.toSeconds(timeWindowBean.toMillis());
        this.postQuery = postQuery;
    }


    private static double calMedian(List<Long> list, int[] helperArray) {
        Arrays.fill(helperArray, 0);
        for (long i : list) {
            helperArray[(int) TimeUnit.MILLISECONDS.toSeconds(i)]++;
        }
        int half = list.size() / 2;
        int count = 0;
        int m = 0;
        for (int i = 0; i < helperArray.length; i++) {
            count += helperArray[i];
            if (count > half) {
                m = i;
                break;
            }
        }
        double median;
        if (list.size() % 2 == 0) {
            if (helperArray[m] > (count - half)) {
                median = (double) m;
            } else {
                int m1 = m;
                while (helperArray[--m1] == 0) {
                }
                median = ((double) m + (double) m1) / 2;
            }
        } else {
            median = (double) m;
        }
        return median;
    }

    @Override
    public QueryResultSet<SwiftNode> getQueryResult() throws SQLException {
        final int[] helpArray = new int[timeWindow + 1];
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
                            for (Map.Entry<FunnelGroupKey, FunnelHelperValue> entry : funnelValue.getValueMap().entrySet()) {
                                List<List<Long>> periods = entry.getValue().getPeriods();
                                for (int i = 0; i < periods.size(); i++) {
                                    List<Long> list = periods.get(i);
                                    if (list.isEmpty()) {
                                        postAggregatorValues.add(new DoubleAmountAggregatorValue(Double.NaN));
                                        continue;
                                    }
                                    postAggregatorValues.add(new DoubleAmountAggregatorValue(calMedian(list, helpArray)));
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
