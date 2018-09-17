package com.fr.swift.query.filter.detail.impl.number;

import com.fr.swift.query.filter.match.MatchConverter;
import com.fr.swift.result.SwiftNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/5/16.
 */
public class NumberAverageFilter extends NumberInRangeFilter {

    // 用来标识是否为平均值
    public static final Double AVG_HOLDER = Double.MIN_VALUE;

    private Map<List, Double> cacheMap;

    public NumberAverageFilter(NumberInRangeFilter filter) {
        super(filter);
    }

    private static Double average(SwiftNode node, int targetIndex) {
        List<SwiftNode> children = node.getParent().getChildren();
        Double sum = .0;
        for (int i = 0; i < children.size(); i++) {
            sum += children.get(i).getAggregatorValue()[targetIndex].calculate();
        }
        return sum / children.size();
    }

    @Override
    public boolean matches(SwiftNode node, int targetIndex, MatchConverter converter) {
        if (cacheMap == null) {
            cacheMap = new HashMap<List, Double>();
        }
        List valueList = new ArrayList();
        SwiftNode parent = node.getParent();
        while (parent != null) {
            valueList.add(parent.getData());
            parent = parent.getParent();
        }
        if (!cacheMap.containsKey(valueList)) {
            cacheMap.put(valueList, average(node, targetIndex));
        }
        Object data = node.getAggregatorValue(targetIndex).calculateValue();
        if (data == null) {
            return false;
        }
        double value = ((Number) data).doubleValue();
        double minValue = min.doubleValue() != Double.NEGATIVE_INFINITY ? cacheMap.get(valueList) : min.doubleValue();
        double maxValue = max.doubleValue() != Double.POSITIVE_INFINITY ? cacheMap.get(valueList) : max.doubleValue();
        return (minIncluded ? value >= minValue : value > minValue) &&
                (maxIncluded ? value <= maxValue : value < maxValue);
    }
}
