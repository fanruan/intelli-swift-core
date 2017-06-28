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
        PrimitiveType type = getter.getPrimitiveType();
        CalculatorTraversalAction ss;
        if (type == PrimitiveType.LONG){
            return calculateLong(getter, range);
        } else if (type == PrimitiveType.DOUBLE){
            return calculateDouble(getter, range);
        } else {
            ss = new CalculatorTraversalAction() {
                @Override
                public void actionPerformed(int row) {
                    Object value = getter.getValue(row);
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
        }
        range.Traversal(ss);
        return ss.getCalculatorValue();
    }

    private double calculateLong(final ICubeColumnDetailGetter getter, GroupValueIndex range) {
        final PrimitiveLongGetter g = (PrimitiveLongGetter) getter.createPrimitiveDetailGetter();
        GroupValueIndex nullIndex = g.getNullIndex();
        CalculatorTraversalAction ss;
        if (!GVIUtils.isAllEmptyRoaringGroupValueIndex(nullIndex)){
            range = range.ANDNOT(nullIndex);
        }
        // 过滤掉空值的gvi
        if (range.isAllEmpty()) {
            // 如果全部为空直接返回最小值的表示
            return NIOConstant.DOUBLE.NULL_VALUE;
        }
        ss = new CalculatorTraversalAction() {
            @Override
            public void actionPerformed(int row) {
                //上面andnot过了，这边不需要判断为null了
                sum += g.getValue(row);
            }

            @Override
            public double getCalculatorValue() {
                return sum;
            }
        };
        range.Traversal(ss);
        return ss.getCalculatorValue();
    }

    private double calculateDouble(final ICubeColumnDetailGetter getter, GroupValueIndex range) {
        final PrimitiveDoubleGetter g = (PrimitiveDoubleGetter) getter.createPrimitiveDetailGetter();
        GroupValueIndex nullIndex = g.getNullIndex();
        CalculatorTraversalAction ss;
        if (!GVIUtils.isAllEmptyRoaringGroupValueIndex(nullIndex)){
            range = range.ANDNOT(nullIndex);
        }
        // 如果全部为空值则直接返回空值的表示就行了
        range = range.ANDNOT(nullIndex);
        if (range.isAllEmpty()) {
            return NIOConstant.DOUBLE.NULL_VALUE;
        }
        ss = new CalculatorTraversalAction() {
            //上面andnot过了，这边不需要判断为null了
            @Override
            public void actionPerformed(int row) {
                sum += g.getValue(row);
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