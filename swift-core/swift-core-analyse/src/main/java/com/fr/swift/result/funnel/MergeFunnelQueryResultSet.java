package com.fr.swift.result.funnel;

import com.fr.swift.query.aggregator.FunnelAggValue;
import com.fr.swift.query.group.FunnelGroupKey;
import com.fr.swift.result.FunnelResultSet;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.IoUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author anchore
 * @date 2019/6/26
 * TODO: 2019/6/26 anchore 结构不好，改！
 */
public class MergeFunnelQueryResultSet implements QueryResultSet<FunnelResultSet>, Serializable {
    private static final long serialVersionUID = -6167409943891683732L;

    private int stepCount;
    private FunnelQueryResultSet mergeResultSet;

    public MergeFunnelQueryResultSet(List<QueryResultSet<FunnelResultSet>> resultSets, int numberOfSteps) {
        stepCount = numberOfSteps;
        init(resultSets, numberOfSteps);
    }

    private void init(List<QueryResultSet<FunnelResultSet>> resultSets, int numberOfSteps) {
        Map<FunnelGroupKey, FunnelAggValue> map = new HashMap<FunnelGroupKey, FunnelAggValue>();
        for (QueryResultSet<FunnelResultSet> resultSet : resultSets) {
            Map<FunnelGroupKey, FunnelAggValue> result = resultSet.getPage().getResult();
            for (Map.Entry<FunnelGroupKey, FunnelAggValue> entry : result.entrySet()) {
                FunnelAggValue contestAggValue = map.get(entry.getKey());
                if (contestAggValue == null) {
                    int[] counter = new int[numberOfSteps];
                    List<List<Long>> periods = createList(numberOfSteps - 1);
                    contestAggValue = new FunnelAggValue(counter, periods);
                    map.put(entry.getKey(), contestAggValue);
                }
                int[] values = entry.getValue().getCount();
                int[] counters = contestAggValue.getCount();
                for (int i = 0; i < values.length; i++) {
                    counters[i] += values[i];
                }
                List<List<Long>> valuePeriods = entry.getValue().getPeriods();
                List<List<Long>> lists = contestAggValue.getPeriods();
                // TODO: 2018/9/25 可以做好一部分排序，以及使用基本类型
                for (int i = 0; i < valuePeriods.size(); i++) {
                    for (int j = 0; j < valuePeriods.get(i).size(); j++) {
                        lists.get(i).add(valuePeriods.get(i).get(j));
                    }
                }
            }
        }

        mergeResultSet = new FunnelQueryResultSet(new FunnelResultSet(map));
    }

    private List<List<Long>> createList(int len) {
        List<List<Long>> lists = new ArrayList<List<Long>>();
        for (int i = 0; i < len; i++) {
            lists.add(new ArrayList<Long>());
        }
        return lists;
    }

    @Override
    public int getFetchSize() {
        return mergeResultSet.getFetchSize();
    }

    @Override
    public SwiftResultSet convert(SwiftMetaData metaData) {
        return mergeResultSet.convert(metaData);
    }

    @Override
    public FunnelResultSet getPage() {
        return mergeResultSet.getPage();
    }

    @Override
    public boolean hasNextPage() {
        return mergeResultSet.hasNextPage();
    }

    @Override
    public void close() {
        IoUtil.close(mergeResultSet);
    }

    public int getStepCount() {
        return stepCount;
    }
}