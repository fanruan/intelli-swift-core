/**
 *
 */
package com.finebi.cube.calculator.bidouble;

import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.CalculatorTraversalAction;

/**
 * @author Daniel
 */
public class SumCalculator implements CubeDoubleDataCalculator {

    public static SumCalculator INSTANCE = new SumCalculator();

    @Override
    public double calculate(final ICubeTableService tableGetterService, final BIKey key, GroupValueIndex range) {
        CalculatorTraversalAction ss = new CalculatorTraversalAction() {
            @Override
            public void actionPerformed(int row) {
                Object value = tableGetterService.getRow(key, row);
                if (value != null) {
                    double v = ((Number) value).doubleValue();
                    sum += v;
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

}