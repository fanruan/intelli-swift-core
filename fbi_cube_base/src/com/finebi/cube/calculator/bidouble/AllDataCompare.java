package com.finebi.cube.calculator.bidouble;

import com.finebi.cube.api.*;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.CalculatorTraversalAction;
import com.fr.bi.stable.io.newio.NIOConstant;

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
        if (type == PrimitiveType.LONG) {
            return calculateLongSum(getter, range);
        } else if (type == PrimitiveType.DOUBLE) {
            return calculateDoubleSum(getter, range);
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

    /**
     * PMD对方法连贯是魔鬼啊。。。。
     *
     * @param getter
     * @param range
     * @return
     */
    private double calculateLongSum(final ICubeColumnDetailGetter getter, GroupValueIndex range) {

        final PrimitiveLongGetter g = (PrimitiveLongGetter) getter.createPrimitiveDetailGetter();
        GroupValueIndex nullIndex = g.getNullIndex();
        CalculatorTraversalAction ss;
        // 空值不参与比较
        if (nullIndex != null) {
            range = range.andnot(nullIndex);
        }
        ss = new CalculatorTraversalAction() {

            long sum = NIOConstant.LONG.NULL_VALUE;

            @Override
            public void actionPerformed(int row) {

                long value = g.getValue(row);
                sum = compare(value, sum);
            }

            @Override
            public double getCalculatorValue() {

                if (sum == NIOConstant.LONG.NULL_VALUE) {
                    return NIOConstant.DOUBLE.NULL_VALUE;
                }
                return sum;
            }
        };
        range.Traversal(ss);
        return ss.getCalculatorValue();
    }

    private double calculateDoubleSum(final ICubeColumnDetailGetter getter, GroupValueIndex range) {

        final PrimitiveDoubleGetter g = (PrimitiveDoubleGetter) getter.createPrimitiveDetailGetter();
        GroupValueIndex nullIndex = g.getNullIndex();
        CalculatorTraversalAction ss;
        // 空值不参与比较
        if (nullIndex != null) {
            range = range.andnot(nullIndex);
        }
        ss = new CalculatorTraversalAction() {

            double sum = NIOConstant.DOUBLE.NULL_VALUE;

            @Override
            public void actionPerformed(int row) {

                double value = g.getValue(row);
                sum = compare(value, sum);
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

    protected abstract long compare(long sum, long rowValue);

}
