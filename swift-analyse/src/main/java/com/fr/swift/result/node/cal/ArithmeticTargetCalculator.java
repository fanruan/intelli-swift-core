package com.fr.swift.result.node.cal;

import com.fr.swift.query.aggregator.AggregatorValue;
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
    private Iterator<AggregatorValue[]> iterator;

    public ArithmeticTargetCalculator(CalTargetType arithmeticType, int[] paramIndexes, int resultIndex, Iterator<AggregatorValue[]> iterator) {
        this.arithmeticType = arithmeticType;
        this.paramIndexes = paramIndexes;
        this.resultIndex = resultIndex;
        this.iterator = iterator;
    }

    @Override
    public Object call() {
        while (iterator.hasNext()) {
            AggregatorValue[] values = iterator.next();
            switch (arithmeticType) {
                case ARITHMETIC_ADD:
                    add(values);
                    break;
                case ARITHMETIC_SUB:
                    sub(values);
                    break;
                case ARITHMETIC_MUL:
                    mul(values);
                    break;
                case ARITHMETIC_DIV:
                    div(values);
                    break;
            }
        }
        return null;
    }

    // TODO: 2018/6/7 这边的简单运算看起来简单考虑到空值确实巨麻烦，难怪druid用0处理空值
    private void add(AggregatorValue[] values) {
        Double sum = null;
        for (int i = 0; i < paramIndexes.length; i++) {
            Double value = values[paramIndexes[i]] == null ? null : values[paramIndexes[i]].calculate();
            sum = value == null ? sum : sum == null ? value : value + sum;
        }
        values[resultIndex] = sum == null ? null : new DoubleAmountAggregatorValue(sum);
    }

    private void sub(AggregatorValue[] values) {
        Double sub = values[paramIndexes[0]] == null ? .0 : values[paramIndexes[0]].calculate();
        for (int i = 0; i < paramIndexes.length; i++) {
            Double value = values[paramIndexes[i]] == null ? .0 : values[paramIndexes[i]].calculate();
            sub -= value;
        }
        values[resultIndex] = new DoubleAmountAggregatorValue(sub);
    }

    private void mul(AggregatorValue[] values) {
        Double mul = null;
        for (int i = 0; i < paramIndexes.length; i++) {
            Double value = values[paramIndexes[i]] == null ? null : values[paramIndexes[i]].calculate();
            mul = value == null ? mul : mul == null ? value : value * mul;
        }
        values[resultIndex] = mul == null ? null : new DoubleAmountAggregatorValue(mul);
    }

    private void div(AggregatorValue[] values) {
        Double div = null;
        for (int i = 0; i < paramIndexes.length; i++) {
            Double value = values[paramIndexes[i]] == null ? null : values[paramIndexes[i]].calculate();
            div = value == null ? div : div == null ? value : div / value;
        }
        values[resultIndex] = div == null ? null : new DoubleAmountAggregatorValue(div);
    }
}
