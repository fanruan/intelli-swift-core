/**
 *
 */
package com.finebi.cube.calculator.bidouble;

import com.fr.bi.stable.io.newio.NIOConstant;

/**
 * @author Daniel
 */
public class MinCalculator extends AllDataCompare {

    public static MinCalculator INSTANCE = new MinCalculator();


    @Override
    protected double compare(double sum, double rowValue) {
        if (Double.isInfinite(sum)) {
            return rowValue;
        } else if (Double.isInfinite(rowValue)) {
            return sum;
        }
        return Math.min(sum, rowValue);
    }

    @Override
    protected long compare(long sum, long rowValue) {
        if (sum == NIOConstant.LONG.NULL_VALUE) {
            return rowValue;
        } else if (rowValue == NIOConstant.LONG.NULL_VALUE) {
            return sum;
        }
        return Math.min(sum, rowValue);
    }
}