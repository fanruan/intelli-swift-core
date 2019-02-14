package com.fr.swift.query.aggregator;

import com.fr.swift.bitmap.traversal.CalculatorTraversalAction;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.FormulaDetailColumn;
import com.fr.swift.segment.column.impl.base.DoubleDetailColumn;
import com.fr.swift.segment.column.impl.base.IntDetailColumn;
import com.fr.swift.segment.column.impl.base.LongDetailColumn;
import com.fr.swift.structure.iterator.RowTraversal;

import java.util.Arrays;


/**
 * @author Xiaolei.liu
 * 方差的计算和平均数计算基本一样，只是求和的数据有差别，
 * 标准差可以由方差得到
 */
public class VarianceAggregate extends AbstractAggregator<VarianceAggregatorValue> {

    protected static final Aggregator INSTANCE = new VarianceAggregate();
    private static final long serialVersionUID = -2346803621309661900L;

    @Override
    public VarianceAggregatorValue aggregate(RowTraversal traversal, Column column) {
        final VarianceAggregatorValue varianceValue = new VarianceAggregatorValue();
        final DetailColumn detailColumn = column.getDetailColumn();
        final Aggregator avg = AverageAggregate.INSTANCE;
        final double[] sum = new double[2];
        Arrays.fill(sum, 0);
        RowTraversal notNullTraversal = getNotNullTraversal(traversal, column);
        if (notNullTraversal.isEmpty()) {
            return varianceValue;
        }
        final double average = avg.aggregate(notNullTraversal, column).calculate();
        CalculatorTraversalAction ss;
        if (detailColumn instanceof LongDetailColumn) {
            return aggregateLong(notNullTraversal, detailColumn, average);
        } else if (detailColumn instanceof DoubleDetailColumn || detailColumn instanceof FormulaDetailColumn) {
            return aggregateDouble(notNullTraversal, detailColumn, average);
        } else {
            final IntDetailColumn idc = (IntDetailColumn) detailColumn;
            ss = new CalculatorTraversalAction() {

                @Override
                public double getCalculatorValue() {
                    return result;
                }

                @Override
                public void actionPerformed(int row) {
                    double source = idc.getInt(row);
                    sum[0] += source;
                    sum[1] += source * source;
                    result += ((source - average) * (source - average));
                }
            };
        }
        notNullTraversal.traversal(ss);
        varianceValue.setSum(sum[0]);
        varianceValue.setSquareSum(sum[1]);
        //求方差时要不要把空值的个数也加上？
        varianceValue.setCount(notNullTraversal.getCardinality());
        varianceValue.setVariance(ss.getCalculatorValue());
        return varianceValue;
    }

    private VarianceAggregatorValue aggregateLong(RowTraversal traversal, final DetailColumn detailColumn, final double average) {
        final VarianceAggregatorValue varianceValue = new VarianceAggregatorValue();
        final LongDetailColumn ldc = (LongDetailColumn) detailColumn;
        CalculatorTraversalAction ss;
        final double[] sum = new double[2];
        Arrays.fill(sum, 0);
        ss = new CalculatorTraversalAction() {

            @Override
            public double getCalculatorValue() {
                return result;
            }

            @Override
            public void actionPerformed(int row) {
                double source = ldc.getLong(row);
                sum[0] += source;
                sum[1] += source * source;
                result += ((source - average) * (source - average));
            }
        };
        traversal.traversal(ss);
        varianceValue.setSum(sum[0]);
        varianceValue.setSquareSum(sum[1]);
        varianceValue.setCount(traversal.getCardinality());
        varianceValue.setVariance(ss.getCalculatorValue());
        return varianceValue;
    }

    private VarianceAggregatorValue aggregateDouble(RowTraversal traversal, final DetailColumn detailColumn, final double average) {
        final VarianceAggregatorValue varianceValue = new VarianceAggregatorValue();
        final double[] sum = new double[2];
        Arrays.fill(sum, 0);
        CalculatorTraversalAction ss;
        ss = new CalculatorTraversalAction() {
            @Override
            public double getCalculatorValue() {
                return result;
            }

            @Override
            public void actionPerformed(int row) {
                double source = detailColumn.getDouble(row);
                sum[0] += source;
                sum[1] += source * source;
                result += ((source - average) * (source - average));
            }
        };
        traversal.traversal(ss);
        varianceValue.setSum(sum[0]);
        varianceValue.setSquareSum(sum[1]);
        varianceValue.setCount(traversal.getCardinality());
        varianceValue.setVariance(ss.getCalculatorValue());
        return varianceValue;
    }

    @Override
    public void combine(VarianceAggregatorValue value, VarianceAggregatorValue other) {
        double sum = value.getSum() + other.getSum();
        double squareSum = value.getSquareSum() + other.getSquareSum();
        int count = value.getCount() + other.getCount();
        double variance = squareSum - sum * sum / (count);
        value.setSum(sum);
        value.setSquareSum(squareSum);
        value.setCount(count);
        value.setVariance(variance);
    }

    @Override
    public VarianceAggregatorValue createAggregatorValue(AggregatorValue value) {
        if (value.calculateValue() == null) {
            return new VarianceAggregatorValue();
        }
        VarianceAggregatorValue varianceAggregatorValue = new VarianceAggregatorValue();
        varianceAggregatorValue.setCount(1);
        varianceAggregatorValue.setSum(value.calculate());
        varianceAggregatorValue.setSquareSum(value.calculate() * value.calculate());
        varianceAggregatorValue.setVariance(0);
        return varianceAggregatorValue;
    }

    @Override
    public AggregatorType getAggregatorType() {
        return AggregatorType.VARIANCE;
    }
}
