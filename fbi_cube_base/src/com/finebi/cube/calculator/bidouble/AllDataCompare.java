package com.finebi.cube.calculator.bidouble;

import com.finebi.cube.api.ICubeColumnDetailGetter;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.CalculatorTraversalAction;

/**
 * This class created on 2016/4/14.
 *
 * @author Connery
 * @since 4.0
 */
public abstract class AllDataCompare implements CubeDoubleDataCalculator {
    @Override
    public double calculate(final ICubeTableService tableGetterService, final BIKey key, GroupValueIndex range) {
        final ICubeColumnDetailGetter getter = tableGetterService.getColumnDetailReader(key);
        CalculatorTraversalAction ss = new CalculatorTraversalAction() {
            boolean firstValue = true;

            @Override
            public void actionPerformed(int row) {
                Object v = getter.getValue(row);
                if (v != null) {
                    double temp = ((Number) v).doubleValue();
                    if (firstValue) {
                        firstValue = false;
                        sum = temp;
                    } else {
                        sum = compare(sum, temp);
                    }
                }
            }

            @Override
            public double getCalculatorValue() {
                return sum;
            }
        };
        range.Traversal(ss);
        return ss.getCalculatorValue();

    }

    protected abstract double compare(double sum, double rowValue);

}
