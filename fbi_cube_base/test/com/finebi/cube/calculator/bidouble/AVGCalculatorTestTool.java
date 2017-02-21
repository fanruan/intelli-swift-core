package com.finebi.cube.calculator.bidouble;

import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.gvi.GroupValueIndex;

/**
 * This class created on 2016/6/24.
 *
 * @author Connery
 * @since 4.0
 */
public class AVGCalculatorTestTool extends AVGCalculator {
    private double sum;

    public AVGCalculatorTestTool(double sum) {
        this.sum = sum;
    }

    @Override
    protected double getSumValue(ICubeTableService tableGetterService, BIKey key, GroupValueIndex range) {
        return sum;
    }
}
