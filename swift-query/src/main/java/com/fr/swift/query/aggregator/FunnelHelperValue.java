package com.fr.swift.query.aggregator;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public class FunnelHelperValue implements Serializable {

    private static final long serialVersionUID = 8578072541928721092L;
    private int[] count;
    private List<List<Long>> periods;
    private double[] medians = new double[0];

    public FunnelHelperValue(int[] count, List<List<Long>> periods) {
        this.count = count;
        this.periods = periods;
    }

    public int[] getCount() {
        return count;
    }

    public List<List<Long>> getPeriods() {
        return periods;
    }

    public double[] getMedians() {
        return medians;
    }

    public void setMedians(double[] medians) {
        this.medians = medians;
    }

    @Override
    public String toString() {
        return "ContestAggValue[" + "count=" + Arrays.toString(count) + ", medians=" + Arrays.toString(medians) + "]";
    }
}
