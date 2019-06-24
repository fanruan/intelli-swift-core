package com.fr.swift.result.node.cal;

import com.fr.swift.query.aggregator.AggregatorValueRow;
import com.fr.swift.query.aggregator.DoubleAmountAggregatorValue;
import com.fr.swift.query.info.bean.type.cal.CalTargetType;

import java.util.Iterator;

/**
 * Created by Lyon on 2018/6/7.
 */
public class ArithmeticTargetCalculator implements TargetCalculator {

    private CalTargetType arithmeticType;
    private int[] paramIndexes;
    private int resultIndex;
    private Iterator<AggregatorValueRow> iterator;

    public ArithmeticTargetCalculator(CalTargetType arithmeticType, int[] paramIndexes, int resultIndex, Iterator<AggregatorValueRow> iterator) {
        this.arithmeticType = arithmeticType;
        this.paramIndexes = paramIndexes;
        this.resultIndex = resultIndex;
        this.iterator = iterator;
    }

    @Override
    public Object call() {
        while (iterator.hasNext()) {
            AggregatorValueRow row = iterator.next();
            switch (arithmeticType) {
                case ARITHMETIC_ADD:
                    add(row);
                    break;
                case ARITHMETIC_SUB:
                    sub(row);
                    break;
                case ARITHMETIC_MUL:
                    mul(row);
                    break;
                case ARITHMETIC_DIV:
                    div(row);
                    break;
            }
        }
        return null;
    }

    // TODO: 2018/6/7 这边的简单运算看起来简单考虑到空值确实巨麻烦，难怪druid用0处理空值
    private void add(AggregatorValueRow values) {
        Double sum = null;
        for (int i = 0; i < paramIndexes.length; i++) {
            Double value = values.getValue(paramIndexes[i]) == null ? null : values.getValue(paramIndexes[i]).calculate();
            sum = value == null ? sum : sum == null ? value : value + sum;
        }
        values.setValue(resultIndex, sum == null ? null : new DoubleAmountAggregatorValue(sum));
    }

    private void sub(AggregatorValueRow values) {
        Double sub = values.getValue(paramIndexes[0]) == null ? .0 : values.getValue(paramIndexes[0]).calculate();
        for (int i = 0; i < paramIndexes.length; i++) {
            Double value = values.getValue(paramIndexes[i]) == null ? .0 : values.getValue(paramIndexes[i]).calculate();
            sub -= value;
        }
        values.setValue(resultIndex, new DoubleAmountAggregatorValue(sub));
    }

    private void mul(AggregatorValueRow values) {
        Double mul = null;
        for (int i = 0; i < paramIndexes.length; i++) {
            Double value = values.getValue(paramIndexes[i]) == null ? null : values.getValue(paramIndexes[i]).calculate();
            mul = value == null ? mul : mul == null ? value : value * mul;
        }
        values.setValue(resultIndex, mul == null ? null : new DoubleAmountAggregatorValue(mul));
    }

    private void div(AggregatorValueRow values) {
        Double div = null;
        for (int i = 0; i < paramIndexes.length; i++) {
            Double value = values.getValue(paramIndexes[i]) == null ? null : values.getValue(paramIndexes[i]).calculate();
            div = value == null ? div : div == null ? value : div / value;
        }
        values.setValue(resultIndex, div == null ? null : new DoubleAmountAggregatorValue(div));
    }
}
