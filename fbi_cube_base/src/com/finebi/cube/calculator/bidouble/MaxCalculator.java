/**
 *
 */
package com.finebi.cube.calculator.bidouble;

/**
 * @author Daniel
 */
public class MaxCalculator extends AllDataCompare {

    public static MaxCalculator INSTANCE = new MaxCalculator();


    @Override
    protected double compare(double sum, double rowValue) {
        return Math.max(sum, rowValue);
    }

    @Override
    protected long compare(long sum, long rowValue) {
        return Math.max(sum, rowValue);
    }

}