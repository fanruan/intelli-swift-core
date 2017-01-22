package com.finebi.cube.calculator.bidouble;

import com.finebi.cube.api.*;
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
        PrimitiveType type = getter.getPrimitiveType();
        CalculatorTraversalAction ss;
        if (type == PrimitiveType.LONG){
            final PrimitiveLongGetter g = (PrimitiveLongGetter) getter.createPrimitiveDetailGetter();
            ss = new CalculatorTraversalAction() {
                boolean firstValue = true;
                long sl;
                @Override
                public void actionPerformed(int row) {
                    long temp = g.getValue(row);
                    if (firstValue) {
                        firstValue = false;
                        sl = temp;
                    } else {
                        sl = compare(sl, temp);
                    }
                }
                @Override
                public double getCalculatorValue() {
                    return sl;
                }
            };
        } else if (type == PrimitiveType.DOUBLE){
            final PrimitiveDoubleGetter g = (PrimitiveDoubleGetter) getter.createPrimitiveDetailGetter();
            ss = new CalculatorTraversalAction() {
                boolean firstValue = true;

                @Override
                public void actionPerformed(int row) {
                    double temp = g.getValue(row);
                    if (firstValue) {
                        firstValue = false;
                        sum = temp;
                    } else {
                        sum = compare(sum, temp);
                    }
                }

                @Override
                public double getCalculatorValue() {
                    return sum;
                }
            };
        } else {
            ss = new CalculatorTraversalAction() {
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
        }
        range.Traversal(ss);
        return ss.getCalculatorValue();


    }

    protected abstract double compare(double sum, double rowValue);

    protected abstract long compare(long sum, long rowValue);

}
