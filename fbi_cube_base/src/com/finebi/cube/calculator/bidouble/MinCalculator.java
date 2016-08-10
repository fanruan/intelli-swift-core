/**
 *
 */
package com.finebi.cube.calculator.bidouble;

/**
 * @author Daniel
 */
public class MinCalculator extends AllDataCompare {

    public static MinCalculator INSTANCE = new MinCalculator();


    @Override
    protected double compare(double sum, double rowValue) {
        return Math.min(sum, rowValue);
    }

    @Override
    protected long compare(long sum, long rowValue) {
        return Math.min(sum, rowValue);
    }
}