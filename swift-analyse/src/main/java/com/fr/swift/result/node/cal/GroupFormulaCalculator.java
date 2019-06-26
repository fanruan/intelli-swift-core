package com.fr.swift.result.node.cal;

import com.fr.swift.query.aggregator.AggregatorValue;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 2018/5/3.
 */
public class GroupFormulaCalculator implements TargetCalculator {

    private String expression;
    private int[] paramIndexes;
    private int resultIndex;
    private Iterator<List<AggregatorValue[]>> iterator;

    public GroupFormulaCalculator(int[] paramIndexes, int resultIndex, String formula,
                                  Iterator<List<AggregatorValue[]>> iterator) {
        this.paramIndexes = paramIndexes;
        this.resultIndex = resultIndex;
        this.expression = formula;
        this.iterator = iterator;
    }

    private String[] paramNames() {
        String[] params = new String[paramIndexes.length];
        for (int i = 0; i < params.length; i++) {
            params[i] = "$" + i;
        }
        return params;
    }

    @Override
    public Object call() throws Exception {
//        Calculator calculator = Calculator.createCalculator();
//        String[] paramNames = paramNames();
//        String formula = FormulaUtils.getParameterIndexEncodedFormula(expression);
//        while (iterator.hasNext()) {
//            List<AggregatorValue[]> row = iterator.next();
//            for (AggregatorValue[] values : row) {
//                updateParameter(calculator, paramNames, values);
//                Object result = calculator.eval(formula);
//                result = result == Primitive.NULL ? null : result;
//                values[resultIndex] = result == null ? null : new DoubleAmountAggregatorValue(((Number) result).doubleValue());
//            }
//        }
        return null;
    }

//    private void updateParameter(Calculator calculator, String[] paramNames, AggregatorValue[] values) {
//        Object[] parameters = new Object[paramIndexes.length];
//        for (int i = 0; i < paramIndexes.length; i++) {
//            parameters[i] = values[paramIndexes[i]].calculateValue();
//        }
//        for (int i = 0; i < parameters.length; i++) {
//            calculator.set(paramNames[i], parameters[i]);
//        }
//    }
}
