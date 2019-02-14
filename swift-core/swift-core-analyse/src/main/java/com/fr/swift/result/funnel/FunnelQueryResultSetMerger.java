package com.fr.swift.result.funnel;

import com.fr.swift.query.aggregator.FunnelAggValue;
import com.fr.swift.query.group.FunnelGroupKey;
import com.fr.swift.result.FunnelResultSet;
import com.fr.swift.result.qrs.QueryResultSetMerger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2018/12/14
 *
 * @author Lucifer
 * @description
 */
public class FunnelQueryResultSetMerger implements QueryResultSetMerger<FunnelResultSet, FunnelQueryResultSet> {

    private static final long serialVersionUID = -8191094955432120770L;
    private int numberOfSteps;

    public FunnelQueryResultSetMerger(int numberOfSteps) {
        this.numberOfSteps = numberOfSteps;
    }

    @Override
    public FunnelQueryResultSet merge(List<FunnelQueryResultSet> resultSets) {
        Map<FunnelGroupKey, FunnelAggValue> map = new HashMap<FunnelGroupKey, FunnelAggValue>();
        for (FunnelQueryResultSet resultSet : resultSets) {
            Map<FunnelGroupKey, FunnelAggValue> result = resultSet.getPage().getResult();
            for (Map.Entry<FunnelGroupKey, FunnelAggValue> entry : result.entrySet()) {
                FunnelAggValue contestAggValue = map.get(entry.getKey());
                if (contestAggValue == null) {
                    int[] counter = new int[numberOfSteps];
                    List<List<Integer>> periods = createList(numberOfSteps - 1);
                    contestAggValue = new FunnelAggValue(counter, periods);
                    map.put(entry.getKey(), contestAggValue);
                }
                int[] values = entry.getValue().getCount();
                int[] counters = contestAggValue.getCount();
                for (int i = 0; i < values.length; i++) {
                    counters[i] += values[i];
                }
                List<List<Integer>> valuePeriods = entry.getValue().getPeriods();
                List<List<Integer>> lists = contestAggValue.getPeriods();
                // TODO: 2018/9/25 可以做好一部分排序，以及使用基本类型
                for (int i = 0; i < valuePeriods.size(); i++) {
                    for (int j = 0; j < valuePeriods.get(i).size(); j++) {
                        lists.get(i).add(valuePeriods.get(i).get(j));
                    }
                }
            }
        }
        return new FunnelQueryResultSet(new FunnelResultSet(map), this);
    }

    private List<List<Integer>> createList(int len) {
        List<List<Integer>> lists = new ArrayList<List<Integer>>();
        for (int i = 0; i < len; i++) {
            lists.add(new ArrayList<Integer>());
        }
        return lists;
    }
}
