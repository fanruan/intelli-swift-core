package com.fr.swift.query.aggregator;

import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * @author Xiaolei.liu
 * 试着写的标准差，方差的计算和平均数计算基本一样，只是求和的数据有差别，
 * 标准差可以由方差得到，中位数需要排序
 */

public class StandarDeviationAggregate  implements Aggregator<StandardAggregatorValue> {

    protected static final Aggregator INSTANCE = new StandarDeviationAggregate();

    @Override
    public StandardAggregatorValue aggregate(RowTraversal traversal, Column column) {
        Aggregator va = VarianceAggregate.INSTANCE;
        StandardAggregatorValue value = new StandardAggregatorValue();
        value.setVariance(((VarianceAggregate)va).aggregate(traversal, column));
        return value;
    }

    @Override
    public void combine(StandardAggregatorValue value, StandardAggregatorValue other) {
        Aggregator va = VarianceAggregate.INSTANCE;
        VarianceAggregatorValue vValue = value.getCalVariance();
        VarianceAggregatorValue oValue = other.getCalVariance();
        va.combine(vValue, oValue);
        value.setVariance(vValue);
    }

}
