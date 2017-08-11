package com.fr.bi.field.target.calculator.sum;

import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.stable.gvi.GroupValueIndex;

/**
 * Created by 小灰灰 on 2015/7/2.
 */
public class MinCalculator extends AbstractSummaryCalculator {
    private static final long serialVersionUID = -6474155469987867904L;

    public MinCalculator(BISummaryTarget target) {
        super(target);
    }


    /**
     * 创建sum值
     *
     * @param gvi 索引
     * @param ti  索引
     * @return double值
     */
    @Override
    public double createSumValue(GroupValueIndex gvi, ICubeTableService ti) {
        return ti.getMINValue(gvi, target.createKey(target.getStatisticElement()));
    }

    @Override
    public double createSumValue(double v1, double v2) {
        return Math.min(v1, v2);
    }

}