package com.fr.swift.query.aggregator;

import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * @author Xiaolei.liu
 * 试着写的标准差，方差的计算和平均数计算基本一样，只是求和的数据有差别，
 * 标准差可以由方差得到，中位数需要排序
 */

public class StandarDeviationAggregate implements Aggregator<StandardAggregatorValue> {

    protected static final Aggregator INSTANCE = new StandarDeviationAggregate();
    private static final long serialVersionUID = -785212630235138823L;

    @Override
    public StandardAggregatorValue aggregate(RowTraversal traversal, Column column) {
        Aggregator va = VarianceAggregate.INSTANCE;
        StandardAggregatorValue value = new StandardAggregatorValue();
        value.setVariance(((VarianceAggregate) va).aggregate(traversal, column));
        return value;
    }

    @Override
    public StandardAggregatorValue createAggregatorValue(AggregatorValue value) {
        StandardAggregatorValue standardAggregatorValue = new StandardAggregatorValue();
//        standardAggregatorValue.setCount(1);
//        standardAggregatorValue.setSum(value.calculate());
//        standardAggregatorValue.setSquareSum(value.calculate() * value.calculate());
//        standardAggregatorValue.setVariance(0);
        VarianceAggregatorValue varianceAggregatorValue = new VarianceAggregatorValue();
        if (value.calculateValue() == null) {
            standardAggregatorValue.setVariance(varianceAggregatorValue);
            return standardAggregatorValue;
        }
        varianceAggregatorValue.setCount(1);
        varianceAggregatorValue.setSum(value.calculate());
        varianceAggregatorValue.setSquareSum(value.calculate() * value.calculate());
        varianceAggregatorValue.setVariance(0);
        standardAggregatorValue.setVariance(varianceAggregatorValue);
        return standardAggregatorValue;
    }

    @Override
    public AggregatorType getAggregatorType() {
        return AggregatorType.STANDARD_DEVIATION;
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
