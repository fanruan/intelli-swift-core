/**
 *
 */
package com.finebi.cube.calculator.bidouble;

import com.finebi.cube.api.*;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.gvi.GVIUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.CalculatorTraversalAction;
import com.fr.bi.stable.io.newio.NIOConstant;

/**
 * @author Daniel
 */
public class SumCalculator implements CubeDoubleDataCalculator {

    public static SumCalculator INSTANCE = new SumCalculator();

    @Override
    public double calculate(final ICubeTableService tableGetterService, final BIKey key, GroupValueIndex range) {
        final ICubeColumnDetailGetter getter = tableGetterService.getColumnDetailReader(key);
        range = range.ANDNOT(tableGetterService.getNullGroupValueIndex(key));
        PrimitiveType type = getter.getPrimitiveType();
        CalculatorTraversalAction ss;
        if (type == PrimitiveType.LONG) {
            final PrimitiveLongGetter g = (PrimitiveLongGetter) getter.createPrimitiveDetailGetter();
            ss = new CalculatorTraversalAction() {
                @Override
                public void actionPerformed(int row) {
                    long value = g.getValue(row);
                    if (Double.isNaN(sum)) {
                        sum = value;
                    } else {
                        sum += value;
                    }
                }

                @Override
                public double getCalculatorValue() {
                    return sum;
                }
            };
        } else if (type == PrimitiveType.DOUBLE) {
            final PrimitiveDoubleGetter g = (PrimitiveDoubleGetter) getter.createPrimitiveDetailGetter();
            ss = new CalculatorTraversalAction() {
                @Override
                public void actionPerformed(int row) {
                    double value = g.getValue(row);
                    if (Double.isNaN(sum)) {
                        sum = value;
                    } else {
                        sum += value;
                    }
                }

                @Override
                public double getCalculatorValue() {
                    return sum;
                }
            };
        } else {
            ss = new CalculatorTraversalAction() {
                @Override
                public void actionPerformed(int row) {
                    Object value = getter.getValue(row);
                    if (value != null) {
                        double v = ((Number) value).doubleValue();
                        if (Double.isNaN(sum)) {
                            sum = v;
                        } else {
                            sum += v;
                        }
                    }
                }

                @Override
                public double getCalculatorValue() {
                    return sum;
                }
            };
        }
        range.Traversal(ss);
        return ss.getCalculatorValue();
    }

}